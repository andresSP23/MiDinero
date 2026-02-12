package com.ansicode.Midinero.dashboard;

import com.ansicode.Midinero.enums.TransactionType;
import com.ansicode.Midinero.transaction.Transaction;
import com.ansicode.Midinero.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final TransactionDashboardRepository transactionDashboardRepository;

    public IncomeExpenseDailyChartsResponse getIncomeAndExpenseChartsByDay(
            LocalDate from,
            LocalDate to,
            Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        List<DailyTotalByTypeRow> rows = transactionDashboardRepository.dailyTotalsByType(user.getId(), fromDateTime,
                toDateTime);

        // Rango de días completo (para que el chart no tenga huecos)
        List<LocalDate> days = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            days.add(cursor);
            cursor = cursor.plusDays(1);
        }

        // Map: tipo -> (día -> total)
        Map<TransactionType, Map<LocalDate, BigDecimal>> totals = new EnumMap<>(TransactionType.class);
        totals.put(TransactionType.INCOME, new HashMap<>());
        totals.put(TransactionType.EXPENSE, new HashMap<>());

        for (LocalDate d : days) {
            totals.get(TransactionType.INCOME).put(d, BigDecimal.ZERO);
            totals.get(TransactionType.EXPENSE).put(d, BigDecimal.ZERO);
        }

        for (DailyTotalByTypeRow r : rows) {
            LocalDate day = r.getDay();
            totals.get(r.getType()).put(day, r.getTotal() == null ? BigDecimal.ZERO : r.getTotal());
        }

        List<DailyChartPointResponse> incomeChart = days.stream()
                .map(d -> new DailyChartPointResponse(d.toString(), totals.get(TransactionType.INCOME).get(d)))
                .toList();

        List<DailyChartPointResponse> expenseChart = days.stream()
                .map(d -> new DailyChartPointResponse(d.toString(), totals.get(TransactionType.EXPENSE).get(d)))
                .toList();

        return new IncomeExpenseDailyChartsResponse(from, to, incomeChart, expenseChart);
    }

    // --- Transacciones recientes ---

    public List<RecentTransactionResponse> getRecentIncomes(int limit, Authentication connectedUser) {
        return getRecentByType(TransactionType.INCOME, limit, connectedUser);
    }

    public List<RecentTransactionResponse> getRecentExpenses(int limit, Authentication connectedUser) {
        return getRecentByType(TransactionType.EXPENSE, limit, connectedUser);
    }

    private List<RecentTransactionResponse> getRecentByType(TransactionType type, int limit,
            Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        List<Transaction> transactions = transactionDashboardRepository
                .findByUserIdAndTransactionTypeOrderByCreatedAtDesc(
                        user.getId(), type, PageRequest.of(0, limit));

        return transactions.stream()
                .map(tx -> RecentTransactionResponse.builder()
                        .id(tx.getId())
                        .description(tx.getDescription())
                        .total(tx.getTotal())
                        .categoryName(tx.getCategory() != null ? tx.getCategory().getName() : "")
                        .createdAt(tx.getCreatedAt())
                        .build())
                .toList();
    }

    // --- Balance general (gráfico pastel) ---

    public BalanceSummaryResponse getBalanceSummary(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        List<Object[]> rows = transactionDashboardRepository.sumTotalsByType(user.getId());

        BigDecimal totalIncomes = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Object[] row : rows) {
            TransactionType type = (TransactionType) row[0];
            BigDecimal total = (BigDecimal) row[1];
            if (type == TransactionType.INCOME) {
                totalIncomes = total;
            } else if (type == TransactionType.EXPENSE) {
                totalExpenses = total;
            }
        }

        BigDecimal balance = totalIncomes.subtract(totalExpenses);

        return new BalanceSummaryResponse(totalIncomes, totalExpenses, balance);
    }
}
