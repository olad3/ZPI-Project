package com.zpi.amoz.dtos;

import com.zpi.amoz.models.ProductVariant;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "DTO reprezentujące szczegóły wariantu produktu, w tym identyfikator, cenę, wymiary, wagę oraz atrybuty wariantu.")
public record ProductVariantDetailsDTO(

        @Schema(description = "Identyfikator wariantu produktu", example = "e7e7d0ff-64a4-45f1-929b-e7e0d6e8e4b5")
        UUID productVariantId,

        @Schema(description = "Kod identyfikacyjny wariantu produktu", example = "12345")
        Integer code,

        @Schema(description = "Informacje o stanie magazynowym wariantu produktu",
                example = "{\"quantity\": 150}")
        StockDTO stock,

        @Schema(description = "Wymiary wariantu produktu",
                example = "{\"length\": 30, \"width\": 20, \"height\": 5}")
        DimensionsDTO dimensions,

        @Schema(description = "Waga wariantu produktu",
                example = "{\"value\": 0.3}")
        WeightDTO weight,

        @Schema(description = "Cena wariantu produktu", example = "199.99")
        BigDecimal variantPrice,

        @Schema(description = "Nazwa wariantu produktu", example = "Czarny T-shirt, rozmiar M")
        String variantName,

        @Schema(description = "Lista atrybutów wariantu produktu",
                example = "[{\"attributeName\": \"Kolor\", \"attributeValue\": \"Czarny\"}, {\"attributeName\": \"Rozmiar\", \"attributeValue\": \"M\"}]")
        List<VariantAttributeDTO> variantAttributes

) {

    public static ProductVariantDetailsDTO toProductVariantDetailsDTO(ProductVariant productVariant) {
        return new ProductVariantDetailsDTO(
                productVariant.getProductVariantId(),
                productVariant.getCode(),
                productVariant.getStock() != null ? StockDTO.toStockDTO(productVariant.getStock()) : null,
                productVariant.getDimensions() != null ? DimensionsDTO.toDimensionsDTO(productVariant.getDimensions()) : null,
                productVariant.getWeight() != null ? WeightDTO.toWeightDTO(productVariant.getWeight()) : null,
                productVariant.getVariantPrice(),
                productVariant.getVariantName(),
                productVariant.getVariantAttributes() != null ? productVariant.getVariantAttributes().stream()
                        .map(VariantAttributeDTO::toVariantAttributeDTO).toList() : List.of()
        );
    }
}