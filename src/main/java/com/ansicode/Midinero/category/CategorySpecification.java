package com.ansicode.Midinero.category;

import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

    public static Specification<Category> withUserId(Long userId) {

        return((root, query, criteriaBuilder) ->  criteriaBuilder.equal(root.get("user").get("id"), userId));
    }
}
