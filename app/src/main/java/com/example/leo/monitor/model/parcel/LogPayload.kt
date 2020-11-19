package com.example.leo.monitor.model.parcel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LogPayload(
    val appHash: String,
    val payload: String
) : Parcelable
