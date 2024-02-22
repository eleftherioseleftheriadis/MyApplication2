package com.example.myapplication2
import com.google.gson.annotations.SerializedName

    data class RecommendationsResponse(val recommendations: List<String>)

    data class ChatGPTRequest(
            val model: String = "text-davinci-003", // Adjust model as necessary
            val prompt: String,
            val temperature: Double = 0.7,
            val max_tokens: Int = 150
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
