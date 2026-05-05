package com.healthlog.myapplication1.data.remote.dto

data class ChatResponse(
    val id: String?,
    val choices: List<Choice>
)

data class Choice(
    val message: ResponseMessage,
    @com.google.gson.annotations.SerializedName("finish_reason")
    val finishReason: String?
)

data class ResponseMessage(
    val role: String,
    val content: String?
)
