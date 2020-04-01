package com.example.leo.monitor.model.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Response<T>(
    val code: Int,
    val message: String,
    val result: T?
)
