package com.example.amoz.models

import com.example.amoz.api.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.util.UUID

@Serializable
data class CategoryTree(
    @Serializable(with = UUIDSerializer::class)
    val categoryId: UUID,
    val name: String,
    val categoryLevel: Short,
    val childCategories: List<CategoryTree> = listOf()
)