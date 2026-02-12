package com.ansicode.Midinero.category;

import com.ansicode.Midinero.user.User;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.stereotype.Service;
@Service
public class CategoryMapper {

    // Crear entidad desde request
    public Category toCategory(CategoryRequest request, User user) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .categoryType(request.getCategoryType())
                .user(user)
                .build();
    }

    // Entity -> Response
    public CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .categoryType(category.getCategoryType())
                .build();
    }

    // Update entity desde request
    public void updateCategoryFromRequest(Category category, CategoryRequest request) {
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setCategoryType(request.getCategoryType());
    }
}
