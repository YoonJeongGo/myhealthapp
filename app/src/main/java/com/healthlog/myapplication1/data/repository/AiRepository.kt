package com.healthlog.myapplication1.data.repository

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.healthlog.myapplication1.data.remote.OpenAiService
import com.healthlog.myapplication1.data.remote.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class AiRepository(private val apiKey: String) {

    private val service: OpenAiService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAiService::class.java)
    }

    private val jsonRegex = Regex("""\{[\s\S]*\}""")

    private val systemPrompt = """
        당신은 영양사입니다. 사용자가 입력한 음식 목록의 칼로리를 추정하세요.
        반드시 아래 JSON 형식으로만 응답하세요. 추가 설명은 하지 마세요.
        {
          "items": [
            {"name": "음식명", "calories": 숫자, "unit": "단위", "isEstimated": true},
            ...
          ],
          "totalCalories": 합계숫자
        }
    """.trimIndent()

    suspend fun estimateMealCalories(rawInput: String): Result<MealCalorieResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = service.chat(
                    auth = "Bearer $apiKey",
                    request = ChatRequest(
                        model = "gpt-4o-mini",
                        messages = listOf(
                            Message("system", systemPrompt),
                            Message("user", "칼로리를 추정해줘: $rawInput")
                        ),
                        maxTokens = 600,
                        temperature = 0.2
                    )
                )

                val rawContent = response.choices.firstOrNull()?.message?.content
                    ?: return@withContext Result.failure(AiException(ErrorType.UNKNOWN))

                val jsonString = jsonRegex.find(rawContent)?.value
                    ?: return@withContext Result.failure(
                        AiException(ErrorType.PARSE_FAILED, rawContent)
                    )

                val parsed = try {
                    Gson().fromJson(jsonString, MealCalorieResponse::class.java)
                } catch (e: JsonSyntaxException) {
                    return@withContext Result.failure(
                        AiException(ErrorType.PARSE_FAILED, rawContent)
                    )
                }

                if (parsed?.items.isNullOrEmpty()) {
                    return@withContext Result.failure(
                        AiException(ErrorType.PARSE_FAILED, rawContent)
                    )
                }

                Result.success(parsed)
            } catch (e: HttpException) {
                val type = if (e.code() == 429) ErrorType.API_LIMIT else ErrorType.UNKNOWN
                Result.failure(AiException(type))
            } catch (e: IOException) {
                Result.failure(AiException(ErrorType.NETWORK_ERROR))
            } catch (e: Exception) {
                Result.failure(AiException(ErrorType.UNKNOWN))
            }
        }
}

class AiException(val type: ErrorType, val rawText: String? = null) : Exception(type.name)
