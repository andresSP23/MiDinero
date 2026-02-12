package com.ansicode.Midinero.auth;

import com.ansicode.Midinero.email.EmailService;
import com.ansicode.Midinero.email.EmailTemplateName;
import com.ansicode.Midinero.handler.BusinessErrorCodes;
import com.ansicode.Midinero.handler.BusinessException;
import com.ansicode.Midinero.role.RolesRepository;
import com.ansicode.Midinero.security.JwtService;
import com.ansicode.Midinero.user.Token;
import com.ansicode.Midinero.user.TokenRepository;
import com.ansicode.Midinero.user.User;
import com.ansicode.Midinero.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Transactional
    public void register(RegistrationRequest request) throws MessagingException {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(BusinessErrorCodes.EMAIL_ALREADY_EXISTS);
        }

        var userRole = rolesRepository.findByName("USER")
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.VALIDATION_ERROR));

        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {

        String newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.fullname(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account Activation");
    }

    private String generateAndSaveActivationToken(User user) {

        String generatedToken = generateActivationCode(6);

        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {

        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    // ========================
    // LOGIN
    // ========================

    @Transactional
    public AuthenticationResponse authenticate(@Valid AuthenticationRequest request) {

        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().trim().toLowerCase(),
                            request.getPassword()));

            var user = (User) auth.getPrincipal();

            var claims = new HashMap<String, Object>();
            claims.put("fullname", user.fullname());

            var jwtToken = jwtService.generateToken(claims, user);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (LockedException e) {
            throw new BusinessException(BusinessErrorCodes.ACCOUNT_LOCKED);

        } catch (DisabledException e) {
            throw new BusinessException(BusinessErrorCodes.ACCOUNT_DISABLED);

        } catch (BadCredentialsException e) {
            throw new BusinessException(BusinessErrorCodes.BAD_CREDENTIALS);

        } catch (AuthenticationException e) {
            throw new BusinessException(BusinessErrorCodes.BAD_CREDENTIALS);
        }
    }

    // ========================
    // ACTIVATE ACCOUNT
    // ========================

    @Transactional
    public void activateAccount(String token) throws MessagingException {

        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.INVALID_TOKEN));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new BusinessException(BusinessErrorCodes.TOKEN_EXPIRED);
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.USER_NOT_FOUND));

        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

}