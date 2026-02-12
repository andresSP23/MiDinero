package com.ansicode.Midinero.dashboard;

import com.ansicode.Midinero.enums.TransactionType;
import com.ansicode.Midinero.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ExcelExportService excelExportService;

    @GetMapping("/income-expense/daily")
    public ResponseEntity<IncomeExpenseDailyChartsResponse> incomeAndExpenseDaily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication connectedUser) {
        return ResponseEntity.ok(dashboardService.getIncomeAndExpenseChartsByDay(from, to, connectedUser));
    }

    @GetMapping("/income/daily")
    public ResponseEntity<DailyChartResponse> incomeDaily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication connectedUser) {
        return ResponseEntity.ok(dashboardService.getDailyIncomeChart(from, to, connectedUser));
    }

    @GetMapping("/expense/daily")
    public ResponseEntity<DailyChartResponse> expenseDaily(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication connectedUser) {
        return ResponseEntity.ok(dashboardService.getDailyExpenseChart(from, to, connectedUser));
    }

    // --- Transacciones recientes ---

    @GetMapping("/recent-incomes")
    public ResponseEntity<List<RecentTransactionResponse>> recentIncomes(
            @RequestParam(defaultValue = "5") int limit,
            Authentication connectedUser) {
        return ResponseEntity.ok(dashboardService.getRecentIncomes(limit, connectedUser));
    }

    @GetMapping("/recent-expenses")
    public ResponseEntity<List<RecentTransactionResponse>> recentExpenses(
            @RequestParam(defaultValue = "5") int limit,
            Authentication connectedUser) {
        return ResponseEntity.ok(dashboardService.getRecentExpenses(limit, connectedUser));
    }

    // --- Exportar a Excel ---

    @GetMapping("/export/incomes")
    public ResponseEntity<byte[]> exportIncomes(Authentication connectedUser) throws IOException {
        User user = (User) connectedUser.getPrincipal();
        byte[] excelBytes = excelExportService.exportTransactionsToExcel(user.getId(), TransactionType.INCOME);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ingresos.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/export/expenses")
    public ResponseEntity<byte[]> exportExpenses(Authentication connectedUser) throws IOException {
        User user = (User) connectedUser.getPrincipal();
        byte[] excelBytes = excelExportService.exportTransactionsToExcel(user.getId(), TransactionType.EXPENSE);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gastos.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    // --- Balance general (gráfico pastel) ---

    @GetMapping("/balance-summary")
    public ResponseEntity<BalanceSummaryResponse> balanceSummary(Authentication connectedUser) {
        return ResponseEntity.ok(dashboardService.getBalanceSummary(connectedUser));
    }
}
