package dev.mottolab.storeapi.controller;

import dev.mottolab.storeapi.dto.request.product.ProductCreateDTO;
import dev.mottolab.storeapi.dto.request.product.ProductUpdateDTO;
import dev.mottolab.storeapi.dto.response.product.ProductDTO;
import dev.mottolab.storeapi.entity.CategoryEntity;
import dev.mottolab.storeapi.entity.ProductEntity;
import dev.mottolab.storeapi.exception.ProductNotExist;
import dev.mottolab.storeapi.repository.CategoryRepository;
import dev.mottolab.storeapi.service.CategoryService;
import dev.mottolab.storeapi.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(
            ProductService productService,
            CategoryService categoryService,
            CategoryRepository categoryRepository
    ){
        this.productService = productService;
        this.categoryService = categoryService;
    }


    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@Valid @RequestBody ProductCreateDTO payload) {
        ProductEntity entity = new ProductEntity();
        entity.setName(payload.name());
        entity.setDescription(payload.description());
        entity.setPrice(payload.price());
        // Create slug
        entity.setSlug(this.productService.createSlugName(payload.name()));
        if(payload.categoryId() != null){
            // Check category exist
            Optional<CategoryEntity> category = this.categoryService.getCategoryById(UUID.fromString(payload.categoryId()));
            category.ifPresent(entity::setCategory);
        }


        return new ProductDTO(this.productService.updateProduct(entity));
    }

    @GetMapping("/getList")
    @ResponseBody
    public List<ProductDTO> getProducts(
            @RequestParam @Min(0) Integer page,
            @RequestParam @Min(1) @Max(100) Integer size
    ) {
        return this.productService.getAllProducts(page, size).stream().map(ProductDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/getListByCategoryId")
    @ResponseBody
    public List<ProductDTO> getProducts(
            @RequestParam UUID category,
            @RequestParam @Min(0) Integer page,
            @RequestParam @Min(1) @Max(100) Integer size
    ) {
        return this.productService.getAllProducts(page, size, category).stream().map(ProductDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/getInfo/slug/{slug}")
    @ResponseBody
    public ProductDTO getProduct(@PathVariable String slug) throws ProductNotExist {
        return new ProductDTO(this.productService.getProductBySlug(slug)
                .orElseThrow(ProductNotExist::new));
    }

    @PutMapping("/getInfo/id/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
    public ProductDTO updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody ProductUpdateDTO payload
    ) throws ProductNotExist {
        ProductEntity entity = this.productService.getProductById(id)
                .orElseThrow(ProductNotExist::new);

        entity.setName(payload.name());
        entity.setDescription(payload.description());
        entity.setPrice(payload.price());
        // Check category
        if(payload.categoryId() != null){
            // Check category exist
            Optional<CategoryEntity> category = this.categoryService.getCategoryById(UUID.fromString(payload.categoryId()));
            category.ifPresent(entity::setCategory);
        }

        return new ProductDTO(this.productService.updateProduct(entity));
    }

    @DeleteMapping("/getInfo/id/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(
            @PathVariable UUID id
    ) throws ProductNotExist {
        // Delete it
        this.productService.deleteProduct(this.productService.getProductById(id)
                .orElseThrow(ProductNotExist::new)
                .getId()
        );
    }
}
