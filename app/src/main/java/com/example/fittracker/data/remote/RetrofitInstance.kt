package com.example.fittracker.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.fittracker.BuildConfig

object RetrofitInstance {
    private const val BASE_URL = "https://trackapi.nutritionix.com/"

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-app-id", BuildConfig.API_ID)
                .addHeader("x-app-key", BuildConfig.API_KEY)
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(NutritionixApi::class.java)
}