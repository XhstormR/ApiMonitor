package com.example.leo.myapplication.model.response

data class Response<T>(
    val code: Int,
    val message: String,
    val result: T
)
