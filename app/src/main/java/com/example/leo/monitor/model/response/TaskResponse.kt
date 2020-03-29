package com.example.leo.monitor.model.response

import com.example.leo.monitor.model.TaskStatus

data class TaskResponse(
    val sha256: String,
    val status: TaskStatus
)
