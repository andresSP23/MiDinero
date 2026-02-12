package com.ansicode.Midinero.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DailyChartResponse {

    private LocalDate from;
    private LocalDate to;
    private List<DailyChartPointResponse> chart;
}
