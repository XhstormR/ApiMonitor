package com.example.leo.monitor.model.request

import java.io.File

data class LogUploadRequest(
    val appHash: String,
    val payload: File
)
