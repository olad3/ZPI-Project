package com.example.amoz.api.repositories

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.amoz.api.extensions.toBitmap
import okhttp3.ResponseBody
import retrofit2.Response

abstract class BaseRepository {
    suspend fun <T> performRequest(request: suspend () -> Response<T>): T? {
        val tag = "${this::class.java.simpleName}.${Thread.currentThread().stackTrace[3].methodName}"
        return try {
            val response: Response<T> = request()
            if (response.isSuccessful) {
                Log.i(tag, "${response.code()} ${response.body()}")
                response.body()
            } else {
                Log.d(tag, "${response.code()} ${response.body()}")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception ${e.message}", e)
            null
        }
    }
}