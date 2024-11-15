package com.zpi.amoz.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Schema(description = "Request do tworzenia wariantu produktu, zawierający dane o produkcie, wariancie, cenie, zapasach, wadze i wymiarach.")
public record ProductVariantCreateRequest(

        @Schema(description = "Identyfikator produktu, którego wariant jest tworzony", example = "e7e7d0ff-64a4-45f1-929b-e7e0d6e8e4b5")
        @NotNull(message = "Product ID is required")
        UUID productID,

        @Schema(description = "Kod wariantu produktu, musi być liczbą dodatnią", example = "12345")
        @Positive(message = "Product variant code must be a positive number")
        @NotNull(message = "Product variant code is required")
        int productVariantCode,

        @Schema(description = "Informacje o stanie magazynowym wariantu produktu")
        @NotNull(message = "Stock information is required")
        StockCreateRequest stock,

        @Schema(description = "Informacje o wadze wariantu produktu (opcjonalne)", nullable = true)
        Optional<WeightCreateRequest> weight,

        @Schema(description = "Wymiary wariantu produktu (opcjonalne)", nullable = true)
        Optional<DimensionsCreateRequest> dimensions,

        @Schema(description = "Lista atrybutów wariantu produktu")
        @NotNull(message = "Variant attributes cannot be null")
        List<AttributeCreateRequest> variantAttributes,

        @Schema(description = "Nazwa wariantu produktu (opcjonalna)", example = "Czarny T-shirt, rozmiar M", nullable = true)
        Optional<@Size(max = 100, message = "Variant name cannot exceed 100 characters") String> variantName,

        @Schema(description = "Cena wariantu produktu, musi być liczbą dodatnią", example = "199.99", nullable = true)
        Optional<@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Price must be a valid amount with up to 10 digits and 2 decimal places") BigDecimal> variantPrice
) {
}