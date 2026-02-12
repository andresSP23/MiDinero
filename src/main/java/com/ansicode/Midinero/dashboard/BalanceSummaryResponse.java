package com.ansicode.Midinero.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BalanceSummaryResponse {

    private BigDecimal totalIncomes;
    private BigDecimal totalExpenses;
    private BigDecimal balance;
}
