package com.example.leo.myapplication.model.parcel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProcessResult(
    val exitValue: Int,
    val content: String
) : Parcelable
