package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    @org.springframework.data.jpa.repository.Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.category WHERE t.user.id = :userId")
    Page<Transaction> findAllByUserId(Long userId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT t FROM Transaction t LEFT JOIN FETCH t.category WHERE t.user.id = :userId AND t.transactionType = :transactionType")
    Page<Transaction> findAllByUserIdAndTransactionType(Long userId, TransactionType transactionType,
            Pageable pageable);

}
