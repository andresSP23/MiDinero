package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> , JpaSpecificationExecutor<Transaction> {


    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
    Page<Transaction> findAllByUserId(Long userId, Pageable pageable);


}
