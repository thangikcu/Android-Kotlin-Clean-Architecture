package com.development.clean.data.remote

import com.development.clean.App
import com.development.clean.data.remote.api.AuthorizationService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator(private val authorizationServiceProvider: () -> AuthorizationService) :
    Authenticator {

    override fun authenticate(route: Route?, response: Response) = runBlocking {
        val accessToken = App.loginUser?.token

        if (accessToken.isNullOrEmpty() || response.retryCount > 2 || !response.isRequestWithAccessToken) {
            return@runBlocking null
        }

        val newAccessToken = authorizationServiceProvider.invoke().refreshToken().data

        App.loginUser = App.loginUser?.copy(token = newAccessToken)

        return@runBlocking response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }
}

val Response.retryCount: Int
    get() {
        var currentResponse = priorResponse
        var result = 0
        while (currentResponse != null) {
            result++
            currentResponse = currentResponse.priorResponse
        }
        return result
    }

val Response.isRequestWithAccessToken: Boolean
    get() {
        val header = request.header("Authorization")
        return header != null && header.startsWith("Bearer")
    }