package com.example.leo.myapplication.model.parcel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DexPayload(
    val appHash: String,
    val payload: ByteArray
) : Parcelable
