package com.ansicode.Midinero.dashboard;

import com.ansicode.Midinero.enums.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class DailyTotalByTypeRow {

    private LocalDate day;
    private TransactionType type;
    private BigDecimal total;

    public DailyTotalByTypeRow(LocalDate day, TransactionType type, BigDecimal total) {
        this.day = day;
        this.type = type;
        this.total = total;
    }
}
