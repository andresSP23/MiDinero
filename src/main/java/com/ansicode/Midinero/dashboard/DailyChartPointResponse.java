package com.ansicode.Midinero.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyChartPointResponse {

    private String date;
    private BigDecimal total;
}
