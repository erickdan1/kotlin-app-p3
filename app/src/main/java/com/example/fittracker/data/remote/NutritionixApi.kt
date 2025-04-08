package com.example.fittracker.data.remote

import com.example.fittracker.data.model.NutritionixRequest
import com.example.fittracker.data.model.NutritionixResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NutritionixApi {
    @POST("v2/natural/exercise")
    suspend fun estimateCalories(@Body request: NutritionixRequest): NutritionixResponse
}