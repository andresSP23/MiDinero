package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.category.Category;
import com.ansicode.Midinero.category.CategoryRepository;
import com.ansicode.Midinero.commom.PageResponse;
import com.ansicode.Midinero.enums.TransactionType;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, Authentication connectedUser) {

        // Obtener usuario autenticado del contexto de seguridad
        User user = (User) connectedUser.getPrincipal();

        // Validar que la categoría exista y pertenezca al usuario para evitar accesos
        // no autorizados (IDOR)
        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.CATEGORY_NOT_FOUND));

        // Mapear DTO a Entidad
        Transaction transaction = transactionMapper.toTransaction(request, user, category);

        // Persistir la transacción
        transaction = transactionRepository.save(transaction);

        return transactionMapper.toTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        // Buscar transacción verificando que pertenezca al usuario autenticado
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.TRANSACTION_NOT_FOUND));

        // Validar que la nueva categoría también pertenezca al usuario
        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.CATEGORY_NOT_FOUND));

        // Actualizar datos de la entidad existente
        transactionMapper.updateTransactionFromRequest(transaction, request, category);

        // Guardar cambios
        transaction = transactionRepository.save(transaction);

        return transactionMapper.toTransactionResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        // Verificar existencia y propiedad de la transacción antes de eliminar
        Transaction transaction = transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.TRANSACTION_NOT_FOUND));

        // Ejecutar borrado lógico (Soft Delete) configurado en la entidad
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> findAllTransactionsByUser(int page, int size,
            TransactionType type,
            Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        // Configurar paginación ordenando por fecha de creación descendente (más
        // recientes primero)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Transaction> transactions;

        // Filtrar por tipo si se especifica, de lo contrario obtener todas
        if (type != null) {
            transactions = transactionRepository.findAllByUserIdAndTransactionType(user.getId(), type, pageable);
        } else {
            transactions = transactionRepository.findAllByUserId(user.getId(), pageable);
        }

        // Mapear entidades a DTOs de respuesta
        List<TransactionResponse> content = transactions.stream()
                .map(transactionMapper::toTransactionResponse)
                .toList();

        // Devuelvo la respuesta paginada estandarizada.
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
