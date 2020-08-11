package com.rafaelanastacioalves.design.concepts.repository.http;


import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response

class AppInterceptor : Interceptor {

    override fun intercept(chain: Chain): Response {
        val original: Request = chain.request();

        // Request customization: add request headers
        val requestBuilder: Request.Builder = original.newBuilder()
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Cache-Control", "no-store");

        val request: Request = requestBuilder.build();
        return chain.proceed(request);
    }
}
