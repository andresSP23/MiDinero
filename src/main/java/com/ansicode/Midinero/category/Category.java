package com.ansicode.Midinero.category;

import com.ansicode.Midinero.commom.AuditedEntity;
import com.ansicode.Midinero.enums.CategoryType;
import com.ansicode.Midinero.transaction.Transaction;
import com.ansicode.Midinero.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Getter
@Setter
@Table(
        name = "category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_user_name", columnNames = {"user_id", "name"})
        }
)
@SuperBuilder
@SQLDelete(sql = "UPDATE category SET is_visible = false WHERE id = ?")
@SQLRestriction("is_visible = true")
@AllArgsConstructor
@NoArgsConstructor
public class Category extends AuditedEntity {

    @Column(nullable = false , length = 50)
    private String name;

    @Column( length = 250)
    private String description;

    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;


    @OneToMany(mappedBy = "category")
    private List<Transaction> transactions;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}
