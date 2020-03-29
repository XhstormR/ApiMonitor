package com.example.leo.monitor.model

enum class TaskStatus(val description: String) {
    virus("有毒"),
    clean("无毒"),
    unknown("未知"),
    pending("正在检测");
}
