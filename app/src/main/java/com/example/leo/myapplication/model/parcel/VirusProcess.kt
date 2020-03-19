package com.example.leo.myapplication.model.parcel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VirusProcess(
    val pid: Int,
    val packageName: String
) : Parcelable
