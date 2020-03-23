package com.example.leo.myapplication.model

import java.io.File

data class LogPayload(
    val appHash: String,
    val payload: File
)
