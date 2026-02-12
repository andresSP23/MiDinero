package com.ansicode.Midinero.category;

import com.ansicode.Midinero.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryRequest {


    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre solo puede tener hasta 50 caracteres")
    private String name;

    @Size(max = 250, message = "La descripcion solo puede tener hasta 250 caracteres")
    private String description;

    @NotNull(message = "El tipo es obligatorio")
    private CategoryType categoryType;
}
