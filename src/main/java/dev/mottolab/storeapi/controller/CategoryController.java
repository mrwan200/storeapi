package dev.mottolab.storeapi.controller;

import dev.mottolab.storeapi.dto.request.category.CategoryCreateDTO;
import dev.mottolab.storeapi.dto.request.category.CategoryUpdateDTO;
import dev.mottolab.storeapi.dto.response.category.CategoryDTO;
import dev.mottolab.storeapi.entity.CategoryEntity;
import dev.mottolab.storeapi.exception.CategoryNotExist;
import dev.mottolab.storeapi.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CATEGORY_CREATE')")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createProduct(@Valid @RequestBody CategoryCreateDTO payload) {
        // Create entity
        CategoryEntity entity = new CategoryEntity();
        entity.setName(payload.name());
        entity.setDescription(payload.description());
        // Create slug
        entity.setSlug(this.categoryService.createSlugName(payload.name()));
        return new CategoryDTO(categoryService.updateCategory(entity));
    }

    @GetMapping
    @ResponseBody
    public List<CategoryDTO> getCategories(
            @RequestParam @Min(0) Integer page,
            @RequestParam @Min(1) @Max(100) Integer size
    ) {
        return this.categoryService
                .getAllCategory(page, size)
                .stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/getInfo/slug/{slug}")
    @ResponseBody
    public CategoryDTO getCategoryInfo(@PathVariable String slug) throws CategoryNotExist {
        return new CategoryDTO(
                this.categoryService.getProductBySlug(slug)
                        .orElseThrow(CategoryNotExist::new)
        );
    }

    @PutMapping("/getInfo/id/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_UPDATE')")
    public CategoryDTO updateProduct(
            @PathVariable UUID id,
            @RequestBody CategoryUpdateDTO payload
    ) throws CategoryNotExist {
        CategoryEntity entity = this.categoryService.getCategoryById(id)
                .orElseThrow(CategoryNotExist::new);

        entity.setName(payload.name());
        entity.setDescription(payload.description());

        return new CategoryDTO(this.categoryService.updateCategory(entity));
    }

    @DeleteMapping("/getInfo/id/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_DELETE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @PathVariable UUID id
    ) throws CategoryNotExist {
        // Delete it
        this.categoryService.deleteCategoryById(
                this.categoryService.getCategoryById(id)
                    .orElseThrow(CategoryNotExist::new)
                    .getId()
        );
    }
}
