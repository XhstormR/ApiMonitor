package com.example.leo.monitor.model.parcel

import android.os.Parcelable
import java.io.File
import kotlinx.parcelize.Parcelize

@Parcelize
data class DexPayload(
    val appHash: String,
    val payload: File
) : Parcelable
