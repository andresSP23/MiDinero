package com.ansicode.Midinero.transaction;

import com.ansicode.Midinero.category.Category;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecification {


    public static Specification<Transaction> withUserId(Long userId) {

        return((root, query, criteriaBuilder) ->  criteriaBuilder.equal(root.get("user").get("id"), userId));
    }
}
