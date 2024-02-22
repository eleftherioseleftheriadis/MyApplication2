package com.example.myapplication2;
import retrofit2.http.GET
import retrofit2.http.Query
import android.widget.Toast
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Call

import com.example.myapplication2.MainActivity


interface ChatGPTApiService {
    @Headers("Authorization: Bearer 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")
    @POST("chat/completions")
    fun getRecommendations(@Body payload: ChatGPTRequest): Call<ChatGPTResponse>
}


