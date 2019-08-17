package com.hari.api.network

import com.hari.api.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Hari Hara Sudhan.N
 */
internal class CountryKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request?.newBuilder()
                ?.addHeader("x-rapidapi-key", BuildConfig.COUNTRY_API_KEY)
                ?.build()
        return chain.proceed(request)
    }
}