package com.healthlog.myapplication1.data.remote

import com.healthlog.myapplication1.data.remote.dto.ChatRequest
import com.healthlog.myapplication1.data.remote.dto.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiService {

    @POST("v1/chat/completions")
    suspend fun chat(
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): ChatResponse
}
