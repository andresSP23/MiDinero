package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.category.Category;
import com.ansicode.Midinero.category.CategoryRepository;
import com.ansicode.Midinero.commom.PageResponse;
import com.ansicode.Midinero.handler.BusinessErrorCodes;
import com.ansicode.Midinero.handler.BusinessException;
import com.ansicode.Midinero.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        // Normalizar
        request.setDescription(request.getDescription().trim());

        // Validaciones
        if (request.getDescription().isBlank()) {
            throw new BusinessException(BusinessErrorCodes.TRANSACTION_DESCRIPTION_REQUIRED);
        }
        if (request.getTotal() == null || request.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(BusinessErrorCodes.TRANSACTION_TOTAL_INVALID);
        }
        if (request.getTransactionType() == null) {
            throw new BusinessException(BusinessErrorCodes.TRANSACTION_TYPE_REQUIRED);
        }
        if (request.getCategoryId() == null) {
            throw new BusinessException(BusinessErrorCodes.TRANSACTION_CATEGORY_REQUIRED);
        }

        // Buscar SOLO categoría del usuario
        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.CATEGORY_NOT_FOUND));

        Transaction transaction = transactionMapper.toTransaction(request, user, category);

        transaction = transactionRepository.save(transaction);

        return transactionMapper.toTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        // Normalizar
        request.setDescription(request.getDescription().trim());

        // Validaciones
        if (request.getDescription().isBlank()) {
            throw new BusinessException(BusinessErrorCodes.TRANSACTION_DESCRIPTION_REQUIRED);
        }
        if (request.getTotal() == null || request.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(BusinessErrorCodes.TRANSACTION_TOTAL_INVALID);
        }
        if (request.getTransactionType() == null) {
            throw new BusinessException(BusinessErrorCodes.TRANSACTION_TYPE_REQUIRED);
        }
        if (request.getCategoryId() == null) {
            throw new BusinessException(BusinessErrorCodes.TRANSACTION_CATEGORY_REQUIRED);
        }

        // Buscar SOLO transacción del usuario
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.TRANSACTION_NOT_FOUND));

        // Buscar SOLO categoría del usuario
        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.CATEGORY_NOT_FOUND));

        transactionMapper.updateTransactionFromRequest(transaction, request, category);

        transaction = transactionRepository.save(transaction);

        return transactionMapper.toTransactionResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.TRANSACTION_NOT_FOUND));

        transactionRepository.delete(transaction); // soft delete si tienes @SQLDelete
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> findAllTransactionsByUser(int page, int size,
            Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Opción 1 (simple): query directa
        Page<Transaction> transactions = transactionRepository.findAllByUserId(user.getId(), pageable);

        List<TransactionResponse> content = transactions.stream()
                .map(transactionMapper::toTransactionResponse)
                .toList();

        return new PageResponse<>(
                content,
                transactions.getNumber(),
                transactions.getSize(),
                transactions.getTotalElements(),
                transactions.getTotalPages(),
                transactions.isFirst(),
                transactions.isLast());
    }

    @Transactional(readOnly = true)
    public TransactionResponse findTransactionById(Long id, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.TRANSACTION_NOT_FOUND));

        return transactionMapper.toTransactionResponse(transaction);
    }

}
