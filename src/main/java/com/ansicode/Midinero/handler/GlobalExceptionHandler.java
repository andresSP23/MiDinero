package com.ansicode.Midinero.handler;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ============================
    // AUTH / SECURITY
    // ============================

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.ACCOUNT_LOCKED.getCode())
                        .businessErrorDescription(BusinessErrorCodes.ACCOUNT_LOCKED.getDescription())
                        .error(exp.getMessage())
                        .build());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.ACCOUNT_DISABLED.getCode())
                        .businessErrorDescription(BusinessErrorCodes.ACCOUNT_DISABLED.getDescription())
                        .error(exp.getMessage())
                        .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.BAD_CREDENTIALS.getCode())
                        .businessErrorDescription(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())
                        .error(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())
                        .build());
    }

    // ✅ JWT válido pero sin permisos/rol
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccessDeniedException exp) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.NO_CODE.getCode())
                        .businessErrorDescription("Access denied")
                        .error("No tienes permisos para realizar esta acción")
                        .build());
    }

    // ✅ Cualquier error de autenticación genérico (token inválido, etc.)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleException(AuthenticationException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.BAD_CREDENTIALS.getCode())
                        .businessErrorDescription(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())
                        .error("No autenticado o sesión inválida")
                        .build());
    }

    // ============================
    // BUSINESS ERRORS (TU APP)
    // ============================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleException(BusinessException exp) {

        BusinessErrorCodes code = exp.getErrorCode();

        if (code == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ExceptionResponse.builder()
                            .businessErrorCode(BusinessErrorCodes.NO_CODE.getCode())
                            .businessErrorDescription("Business error")
                            .error(exp.getMessage() != null ? exp.getMessage() : "Business rule violated")
                            .build());
        }

        HttpStatus status = code.getHttpStatus() != null ? code.getHttpStatus() : HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(status)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(code.getCode())
                        .businessErrorDescription(code.getDescription())
                        .error(exp.getMessage() != null ? exp.getMessage() : code.getDescription())
                        .build());
    }

    // ============================
    // VALIDATION (DTO @Valid)
    // ============================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {

        Set<String> validationErrors = new HashSet<>();
        Map<String, String> fieldErrors = new HashMap<>();

        exp.getBindingResult().getFieldErrors().forEach(err -> {
            String field = err.getField();
            String message = err.getDefaultMessage();
            fieldErrors.put(field, message);
            validationErrors.add(message);
        });

        exp.getBindingResult().getGlobalErrors().forEach(err -> {
            String message = err.getDefaultMessage();
            validationErrors.add(message);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.VALIDATION_ERROR.getCode())
                        .businessErrorDescription(BusinessErrorCodes.VALIDATION_ERROR.getDescription())
                        .validationErrors(validationErrors)
                        .errors(fieldErrors)
                        .error("Validation error")
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleException(ConstraintViolationException exp) {

        Set<String> validationErrors = new HashSet<>();
        exp.getConstraintViolations().forEach(v -> validationErrors.add(v.getMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.VALIDATION_ERROR.getCode())
                        .businessErrorDescription(BusinessErrorCodes.VALIDATION_ERROR.getDescription())
                        .validationErrors(validationErrors)
                        .error("Validation error")
                        .build());
    }

    // ✅ JSON inválido / parseo de fechas / body vacío mal formado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleException(HttpMessageNotReadableException exp) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.VALIDATION_ERROR.getCode())
                        .businessErrorDescription(BusinessErrorCodes.VALIDATION_ERROR.getDescription())
                        .error("JSON inválido o formato incorrecto en el body (revisa fechas y tipos)")
                        .build());
    }

    // ✅ /clientes/{id} cuando mandan "abc" en vez de número
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentTypeMismatchException exp) {
        String param = exp.getName();
        String value = exp.getValue() != null ? exp.getValue().toString() : "null";

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.VALIDATION_ERROR.getCode())
                        .businessErrorDescription(BusinessErrorCodes.VALIDATION_ERROR.getDescription())
                        .error("Parámetro inválido: " + param + " = " + value)
                        .build());
    }

    // ✅ falta ?page= o ?size= etc.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleException(MissingServletRequestParameterException exp) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.VALIDATION_ERROR.getCode())
                        .businessErrorDescription(BusinessErrorCodes.VALIDATION_ERROR.getDescription())
                        .error("Falta el parámetro requerido: " + exp.getParameterName())
                        .build());
    }

    // ============================
    // NOT FOUND
    // ============================

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.NO_CODE.getCode())
                        .businessErrorDescription("Not Found")
                        .error(exp.getMessage())
                        .build());
    }

    // ============================
    // DB CONSTRAINTS
    // ============================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleException(DataIntegrityViolationException exp) {

        // log útil (sin romper respuesta)
        log.warn("DataIntegrityViolationException: {}", exp.getMostSpecificCause() != null
                ? exp.getMostSpecificCause().getMessage()
                : exp.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.NO_CODE.getCode())
                        .businessErrorDescription("Data integrity violation")
                        .error("No se pudo completar la operación por una restricción de datos (por ejemplo, valores duplicados).")
                        .build());
    }

    // ============================
    // EMAIL / INFRA
    // ============================

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        log.error("MessagingException", exp);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.NO_CODE.getCode())
                        .businessErrorDescription("Email error")
                        .error("No se pudo enviar el correo")
                        .build());
    }

    // ============================
    // GENERIC (AL FINAL)
    // ============================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {

        log.error("Unhandled exception", exp);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.NO_CODE.getCode())
                        .businessErrorDescription("Internal Server Error")
                        .error("Ocurrió un error inesperado")
                        .build());
    }



    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalArgumentException exp) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.VALIDATION_ERROR.getCode())
                        .businessErrorDescription("Invalid request")
                        .error(exp.getMessage())
                        .build());
    }



    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleException(IllegalStateException exp) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .businessErrorCode(BusinessErrorCodes.NO_CODE.getCode())
                        .businessErrorDescription("Invalid operation")
                        .error(exp.getMessage())
                        .build());
    }
}
