package com.example.leo.myapplication.model.response

import com.example.leo.myapplication.model.TaskStatus

data class TaskResponse(
    val sha256: String,
    val status: TaskStatus
)
