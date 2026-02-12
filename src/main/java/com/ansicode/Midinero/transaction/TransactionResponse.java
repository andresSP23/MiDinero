package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private String description;
    private BigDecimal total;
    private TransactionType transactionType;

    private Long categoryId;
    private String categoryName;

    private LocalDateTime createdAt;



}
