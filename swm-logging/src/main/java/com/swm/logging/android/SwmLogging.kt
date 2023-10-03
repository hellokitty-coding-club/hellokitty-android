package com.swm.logging.android

import com.google.gson.GsonBuilder
import com.swm.logging.android.logging_scheme.ExposureLogging
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder
import java.util.concurrent.TimeUnit


object SwmLogging {

    private const val AUTHORIZATION = "Authorization"
    private lateinit var appVersion: String
    private lateinit var osName: String
    private lateinit var baseUrl: String
    private lateinit var endpoint: String
    private lateinit var accessToken: String
    private lateinit var loggingService: LoggingService


    suspend fun shotExposureLogging(exposureLogging: ExposureLogging): Response<BaseDTO> {
        checkInitialized()
        return loggingService.postExposureLogging(endpoint, exposureLogging)
    }

    private fun checkInitialized() {
        check(::baseUrl.isInitialized) {
            "The 'baseUrl' property must be initialized. Initialize it by calling the 'init' method on the SwmLogging instance within your Application class."
        }
    }

    fun init(appVersion: String, osName: String, baseUrl: String, endpoint: String, token: String) {
        this.appVersion = appVersion
        this.osName = osName
        this.baseUrl = baseUrl
        val encodedUrl = URLEncoder.encode(endpoint, "UTF-8")
        this.endpoint = encodedUrl
        this.accessToken = token
        setLoggingService()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }

    private val interceptor: Interceptor =
        Interceptor { chain ->
            with(chain) {
                proceed(
                    request().newBuilder().addHeader(AUTHORIZATION, accessToken).build()
                )
            }
        }

    private val loggingRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    private fun setLoggingService() {
        this.loggingService = loggingRetrofit.create(LoggingService::class.java)
    }
}
