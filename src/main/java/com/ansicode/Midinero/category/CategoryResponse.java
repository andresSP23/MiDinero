package com.ansicode.Midinero.category;

import com.ansicode.Midinero.enums.CategoryType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryResponse {

    private Long id;

    private String name;

    private String description;

    private CategoryType categoryType;


}
