package com.zpi.amoz.dtos;

import com.zpi.amoz.models.ProductOrderItem;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Schema(description = "DTO reprezentujące podsumowanie pozycji zamówienia, zawierające informacje o wariancie produktu, cenie jednostkowej oraz ilości.")
public record ProductOrderItemSummaryDTO(

        @Schema(description = "Identyfikator pozycji zamówienia", example = "a2e9f5c4-4d52-40b3-bf3b-ecbb94c79e7b")
        UUID productOrderItemId,

        @Schema(description = "Podsumowanie wariantu produktu", implementation = ProductVariantSummaryDTO.class)
        ProductVariantSummaryDTO productVariant,

        @Schema(description = "Cena jednostkowa produktu", example = "299.99")
        BigDecimal unitPrice,

        @Schema(description = "Ilość zamówionych produktów", example = "2")
        int amount,

        @Schema(description = "Nazwa produktu", example = "\"Koszulka, Czerwona\"")
        String productName

) {

    public static ProductOrderItemSummaryDTO toProductOrderItemSummaryDTO(ProductOrderItem productOrderItem) {
        return new ProductOrderItemSummaryDTO(
                productOrderItem.getProductOrderItemId(),
                ProductVariantSummaryDTO.toProductVariantSummaryDTO(productOrderItem.getProductVariant()),
                productOrderItem.getUnitPrice(),
                productOrderItem.getAmount(),
                productOrderItem.getProductName()
        );
    }
}


