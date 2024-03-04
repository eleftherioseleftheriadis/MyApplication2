package com.example.myapplication2
import com.google.gson.annotations.SerializedName

    data class RecommendationsResponse(val recommendations: List<String>)

data class ChatGPTRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double,
    val max_tokens: Int
)

data class Message(
    val role: String,
    val content: String
)

data class ChatGPTResponse(
    @SerializedName("id") val id: String,
    //@SerializedName("object") val object: String,
    @SerializedName("created") val created: Int,
    @SerializedName("model") val model: String,
    @SerializedName("choices") val choices: List<Choice>
)

data class Choice(
    @SerializedName("text") val text: String,
    @SerializedName("index") val index: Int,
    @SerializedName("logprobs") val logprobs: Any?, // Adjust the type as necessary
    @SerializedName("finish_reason") val finish_reason: String
)
