package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.category.Category;
import com.ansicode.Midinero.commom.AuditedEntity;
import com.ansicode.Midinero.enums.CategoryType;
import com.ansicode.Midinero.enums.TransactionType;
import com.ansicode.Midinero.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "transaction")
@SuperBuilder
@SQLDelete(sql = "UPDATE transaction SET is_visible = false WHERE id = ?")
@SQLRestriction("is_visible = true")
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends AuditedEntity {

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id", nullable = false)
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Category category;

}
