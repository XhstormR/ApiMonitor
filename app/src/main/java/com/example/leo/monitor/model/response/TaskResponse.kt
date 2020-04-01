package com.example.leo.monitor.model.response

import com.example.leo.monitor.model.TaskStatus
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TaskResponse(
    val sha256: String,
    val status: TaskStatus
)
