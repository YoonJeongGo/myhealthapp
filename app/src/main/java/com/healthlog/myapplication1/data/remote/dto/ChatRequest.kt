package com.healthlog.myapplication1.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    @SerializedName("max_tokens") val maxTokens: Int = 600,
    val temperature: Double = 0.2
)

data class Message(
    val role: String,
    val content: String
)
