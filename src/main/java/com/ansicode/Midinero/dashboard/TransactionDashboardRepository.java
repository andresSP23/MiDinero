package com.ansicode.Midinero.dashboard;

import com.ansicode.Midinero.enums.TransactionType;
import com.ansicode.Midinero.transaction.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionDashboardRepository extends JpaRepository<Transaction, Long> {

    @Query("""
                select new com.ansicode.Midinero.dashboard.DailyTotalByTypeRow(
                    cast(t.createdAt as LocalDate),
                    t.transactionType,
                    coalesce(sum(t.total), 0)
                )
                from Transaction t
                where t.user.id = :userId
                  and t.createdAt between :from and :to
                group by cast(t.createdAt as LocalDate), t.transactionType
                order by cast(t.createdAt as LocalDate)
            """)
    List<DailyTotalByTypeRow> dailyTotalsByType(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    // Últimas transacciones por tipo (para dashboard recientes)
    List<Transaction> findByUserIdAndTransactionTypeOrderByCreatedAtDesc(
            Long userId,
            TransactionType transactionType,
            Pageable pageable);

    // Todas las transacciones por tipo (para exportar Excel)
    List<Transaction> findByUserIdAndTransactionTypeOrderByCreatedAtDesc(
            Long userId,
            TransactionType transactionType);

    // Totales por tipo (para gráfico pastel de balance)
    @Query("""
                select t.transactionType, coalesce(sum(t.total), 0)
                from Transaction t
                where t.user.id = :userId
                group by t.transactionType
            """)
    List<Object[]> sumTotalsByType(@Param("userId") Long userId);
}
