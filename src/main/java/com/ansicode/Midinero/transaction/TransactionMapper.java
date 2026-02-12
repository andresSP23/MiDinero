package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.category.Category;
import com.ansicode.Midinero.user.User;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.stereotype.Service;

@Service
public class TransactionMapper {


    // Crear entidad desde request
    public Transaction toTransaction(TransactionRequest request,
                                     User user,
                                     Category category) {

        return Transaction.builder()
                .description(request.getDescription())
                .total(request.getTotal())
                .transactionType(request.getTransactionType())
                .user(user)
                .category(category)
                .build();
    }

    // Entity -> Response
    public TransactionResponse toTransactionResponse(Transaction transaction) {

        return TransactionResponse.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .total(transaction.getTotal())
                .transactionType(transaction.getTransactionType())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .createdAt(transaction.getCreatedAt())
                .build();
    }


    public void updateTransactionFromRequest(Transaction transaction,
                                             TransactionRequest request,
                                             Category category) {

        transaction.setDescription(request.getDescription());
        transaction.setTotal(request.getTotal());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setCategory(category);
    }
}
