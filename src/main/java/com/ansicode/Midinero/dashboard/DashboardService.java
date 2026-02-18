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

    // Obtener datos para gráficas de Ingresos vs Gastos agrupados por día
    public IncomeExpenseDailyChartsResponse getIncomeAndExpenseChartsByDay(
            LocalDate from,
            LocalDate to,
            Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        // Convertir limites de fecha a LocalDateTime para abarcar el día completo
        // (00:00 - 23:59)
        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        // Consultar totales agrupados desde el repositorio
        List<DailyTotalByTypeRow> rows = transactionDashboardRepository.dailyTotalsByType(user.getId(), fromDateTime,
                toDateTime);

        // Generar lista continua de días para evitar huecos en la gráfica
        List<LocalDate> days = buildDayRange(from, to);

        // Organizar datos en un mapa para acceso rápido
        Map<TransactionType, Map<LocalDate, BigDecimal>> totals = buildTotalsMap(days, rows);

        // Construir lista de puntos para la serie de Ingresos
        List<DailyChartPointResponse> incomeChart = days.stream()
                .map(d -> new DailyChartPointResponse(d.toString(), totals.get(TransactionType.INCOME).get(d)))
                .toList();

        // Construir lista de puntos para la serie de Gastos
        List<DailyChartPointResponse> expenseChart = days.stream()
                .map(d -> new DailyChartPointResponse(d.toString(), totals.get(TransactionType.EXPENSE).get(d)))
                .toList();

        return new IncomeExpenseDailyChartsResponse(from, to, incomeChart, expenseChart);
    }

    public DailyChartResponse getDailyIncomeChart(LocalDate from, LocalDate to, Authentication connectedUser) {
        return getDailyChartByType(TransactionType.INCOME, from, to, connectedUser);
    }

    public DailyChartResponse getDailyExpenseChart(LocalDate from, LocalDate to, Authentication connectedUser) {
        return getDailyChartByType(TransactionType.EXPENSE, from, to, connectedUser);
    }

    // Método auxiliar para generar gráfica de un solo tipo
    private DailyChartResponse getDailyChartByType(TransactionType type, LocalDate from, LocalDate to,
            Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        List<DailyTotalByTypeRow> rows = transactionDashboardRepository.dailyTotalsByType(user.getId(), fromDateTime,
                toDateTime);

        List<LocalDate> days = buildDayRange(from, to);
        Map<TransactionType, Map<LocalDate, BigDecimal>> totals = buildTotalsMap(days, rows);

        List<DailyChartPointResponse> chart = days.stream()
                .map(d -> new DailyChartPointResponse(d.toString(), totals.get(type).get(d)))
                .toList();

        return new DailyChartResponse(from, to, chart);
    }

    // Crear rango secuencial de fechas
    private List<LocalDate> buildDayRange(LocalDate from, LocalDate to) {
        List<LocalDate> days = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            days.add(cursor);
            cursor = cursor.plusDays(1);
        }
        return days;
    }

    // Transformar lista plana de BD a estructura Map, inicializando días vacíos en
    // cero
    private Map<TransactionType, Map<LocalDate, BigDecimal>> buildTotalsMap(
            List<LocalDate> days, List<DailyTotalByTypeRow> rows) {
        Map<TransactionType, Map<LocalDate, BigDecimal>> totals = new EnumMap<>(TransactionType.class);
        totals.put(TransactionType.INCOME, new HashMap<>());
        totals.put(TransactionType.EXPENSE, new HashMap<>());

        // Inicializar todas las fechas en cero
        for (LocalDate d : days) {
            totals.get(TransactionType.INCOME).put(d, BigDecimal.ZERO);
            totals.get(TransactionType.EXPENSE).put(d, BigDecimal.ZERO);
        }

        // Asignar valores reales encontrados
        for (DailyTotalByTypeRow r : rows) {
            totals.get(r.getType()).put(r.getDay(), r.getTotal() == null ? BigDecimal.ZERO : r.getTotal());
        }
        return totals;
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

        // Limitar resultados mediante PageRequest
        List<Transaction> transactions = transactionDashboardRepository
                .findByUserIdAndTransactionTypeOrderByCreatedAtDesc(
                        user.getId(), type, PageRequest.of(0, limit));

        return transactions.stream()
                .map(tx -> RecentTransactionResponse.builder()
                        .id(tx.getId())
                        .description(tx.getDescription())
                        .total(tx.getTotal())
                        // Manejo seguro de nulos en caso de categoría eliminada
                        .categoryName(tx.getCategory() != null ? tx.getCategory().getName() : "Categoría eliminada")
                        .createdAt(tx.getCreatedAt())
                        .build())
                .toList();
    }

    // --- Balance general (gráfico pastel) ---

    // Calcular totales históricos acumulados
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
