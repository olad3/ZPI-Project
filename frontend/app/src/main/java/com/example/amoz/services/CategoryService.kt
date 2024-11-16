package com.example.amoz.services

import com.example.amoz.models.CategoryDetails
import com.example.amoz.models.CategoryTree
import com.example.amoz.requests.CategoryCreateRequest
import com.example.amoz.responses.MessageResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface CategoryService {

    @POST("api/categories")
    suspend fun createCategory(@Body categoryCreateRequest: CategoryCreateRequest): Response<JsonElement>

    @PUT("api/categories/{categoryId}")
    suspend fun updateCategory(
        @Path("categoryId") categoryId: UUID,
        @Body categoryCreateRequest: CategoryCreateRequest
    ): Response<JsonElement>

    @DELETE("api/categories/{categoryId}")
    suspend fun deleteCategory(@Path("categoryId") categoryId: UUID): Response<MessageResponse>

    @GET("api/categories")
    suspend fun getAllCompanyCategories(): Response<JsonElement>
}
