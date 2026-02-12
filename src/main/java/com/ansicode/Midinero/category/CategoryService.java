package com.ansicode.Midinero.category;

import com.ansicode.Midinero.commom.PageResponse;
import com.ansicode.Midinero.handler.BusinessErrorCodes;
import com.ansicode.Midinero.handler.BusinessException;
import com.ansicode.Midinero.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        // Normalizar
        categoryRequest.setName(categoryRequest.getName().trim());

        // Validar duplicado por usuario
        if (categoryRepository.existsByUserIdAndNameIgnoreCase(user.getId(), categoryRequest.getName())) {
            throw new BusinessException(BusinessErrorCodes.CATEGORY_ALREADY_EXISTS);
        }

        Category category = categoryMapper.toCategory(categoryRequest, user);

        category = categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        // Normalizar
        request.setName(request.getName().trim());

        // Buscar SOLO del usuario
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.CATEGORY_NOT_FOUND));

        // Validar duplicado (solo si está cambiando el nombre)
        if (!category.getName().equalsIgnoreCase(request.getName())
                && categoryRepository.existsByUserIdAndNameIgnoreCase(user.getId(), request.getName())) {
            throw new BusinessException(BusinessErrorCodes.CATEGORY_ALREADY_EXISTS);
        }

        categoryMapper.updateCategoryFromRequest(category, request);

        category = categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    @Transactional
    public void deleteCategory(Long id, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.CATEGORY_NOT_FOUND));

        categoryRepository.delete(category); // soft delete por @SQLDelete
    }

    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> findAllCategoriesByUser(int page, int size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Category> categories = categoryRepository.findAll(
                CategorySpecification.withUserId(user.getId()),
                pageable);

        List<CategoryResponse> content = categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();

        return new PageResponse<>(
                content,
                categories.getNumber(),
                categories.getSize(),
                categories.getTotalElements(),
                categories.getTotalPages(),
                categories.isFirst(),
                categories.isLast());
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategoryById(Long id, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCodes.CATEGORY_NOT_FOUND));

        return categoryMapper.toCategoryResponse(category);
    }

}
