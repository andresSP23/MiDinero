package com.ansicode.Midinero.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class RecentTransactionResponse {

    private Long id;
    private String description;
    private BigDecimal total;
    private String categoryName;
    private LocalDateTime createdAt;
}
