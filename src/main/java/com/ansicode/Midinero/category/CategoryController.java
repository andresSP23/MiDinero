package com.ansicode.Midinero.category;

import com.ansicode.Midinero.commom.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name="Category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create")

    public ResponseEntity<CategoryResponse> create (@RequestBody @Valid CategoryRequest categoryRequest , Authentication connectedUser){

        return ResponseEntity.ok(categoryService.createCategory(categoryRequest , connectedUser));


    }


    @GetMapping("/findAll")

    public ResponseEntity<PageResponse<CategoryResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @ParameterObject Pageable pageable,
            Authentication connectedUser
    ){

return ResponseEntity.ok(categoryService.findAllCategoriesByUser(page , size ,connectedUser));

    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<CategoryResponse> findById(
                @PathVariable Long id,
                Authentication connectedUser)
    {

        return ResponseEntity.ok(categoryService.findCategoryById(id, connectedUser));

    }

    @PutMapping("update/{id}")

    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @RequestBody @Valid CategoryRequest categoryRequest , Authentication connectedUser){


        return ResponseEntity.ok(categoryService.updateCategory (id, categoryRequest  , connectedUser));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication connectedUser){

        categoryService.deleteCategory(id, connectedUser);

        return  ResponseEntity.noContent().build();

    }

}
