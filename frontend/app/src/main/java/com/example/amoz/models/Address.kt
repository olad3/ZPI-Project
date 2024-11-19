package com.example.amoz.models

import com.example.amoz.api.serializers.UUIDSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Address(
    @Serializable(with = UUIDSerializer::class)
    val addressId: UUID,
    val city: String,
    val street: String,
    val streetNumber: String,
    val apartmentNumber: String? = null,
    val postalCode: String,
    val additionalInformation: String? = null
)
