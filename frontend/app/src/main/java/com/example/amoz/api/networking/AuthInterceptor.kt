package com.example.amoz.api.networking

import android.util.Log
import com.example.amoz.api.managers.GoogleAuthManager
import com.example.amoz.api.managers.TokenManager
import com.example.amoz.api.repositories.AuthenticationRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authenticationRepository: AuthenticationRepository,
    private val googleAuthManager: GoogleAuthManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        val accessToken = tokenManager.getAccessToken()

        if (tokenManager.accessTokenTTLSeconds < 300) {
            runBlocking {
                val refreshToken = tokenManager.getRefreshToken()
                if (!refreshToken.isNullOrEmpty()) {
                    val tokens = authenticationRepository.refreshAccessToken(refreshToken)
                    if (tokens?.accessToken == null) {
                        showGoogleSignInActivity()
                        if (!accessToken.isNullOrEmpty()) {
                            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
                        }

                    } else {
                        tokenManager.saveTokens(tokens.accessToken, tokens.refreshToken)
                        if (!accessToken.isNullOrEmpty()) {
                            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
                        }
                    }
                } else {
                    showGoogleSignInActivity()
                    if (!accessToken.isNullOrEmpty()) {
                        requestBuilder.addHeader("Authorization", "Bearer $accessToken")
                    }

                }
            }
        } else {
            if (!accessToken.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $accessToken")
            }
        }
        return chain.proceed(requestBuilder.build())
    }

    private suspend fun showGoogleSignInActivity() {
        tokenManager.clearTokens()
        Log.i("interceptor", "start activity")

        return suspendCancellableCoroutine { continuation ->
            googleAuthManager.startSignInActivity {
                try {
                    continuation.resume(Unit)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
        }
    }
}