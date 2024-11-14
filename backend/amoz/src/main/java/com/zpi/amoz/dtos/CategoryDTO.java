package com.zpi.amoz.dtos;

import com.zpi.amoz.models.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;
import java.util.UUID;

@Schema(description = "Obiekt reprezentujący kategorię, w tym dane o poziomie kategorii i opcjonalnej kategorii nadrzędnej")
public record CategoryDTO(

        @Schema(description = "ID kategorii", example = "e4b5fa0f-b8b1-4b60-bb6b-e4b3d91811ed")
        UUID categoryId,

        @Schema(description = "Nazwa kategorii", example = "Elektronika")
        String name,

        @Schema(description = "Poziom kategorii w hierarchii", example = "1")
        short categoryLevel,

        @Schema(description = "Kategoria nadrzędna, jeśli istnieje",
                example = "{ \"categoryId\": \"e4b5fa0f-b8b1-4b60-bb6b-e4b3d91811ed\", \"name\": \"Technologia\", \"categoryLevel\": 2, \"parentCategory\": null }",
                nullable = true)
        Optional<CategoryDTO> parentCategory
) {
    public static CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(
                category.getCategoryId(),
                category.getName(),
                category.getCategoryLevel(),
                Optional.ofNullable(category.getParentCategory() != null ? toCategoryDTO(category.getParentCategory()) : null)
        );
    }
}


