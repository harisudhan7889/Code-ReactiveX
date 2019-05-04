package com.hari.transformoperators.network

import com.hari.transformoperators.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Hari Hara Sudhan.N
 */
internal class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request?.newBuilder()
            ?.addHeader("user-key", BuildConfig.ZOMATO_API_KEY)
            ?.build()
        return chain.proceed(request)
    }
}