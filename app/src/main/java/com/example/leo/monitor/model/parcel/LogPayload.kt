package com.example.leo.monitor.model.parcel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LogPayload(
    val appHash: String,
    val payload: String
) : Parcelable
