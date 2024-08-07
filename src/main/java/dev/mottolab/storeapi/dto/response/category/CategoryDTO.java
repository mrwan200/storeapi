package dev.mottolab.storeapi.dto.response.category;

import dev.mottolab.storeapi.enitity.CategoryEntity;
import lombok.Getter;

@Getter
public class CategoryDTO {
    private final Integer id;
    private final String name;
    private final String description;
    private final String slug;

    public CategoryDTO(CategoryEntity entity) {
        this.name = entity.getName();
        this.slug = entity.getSlug();
        this.id = entity.getId();
        this.description = entity.getDescription();
    }
}
