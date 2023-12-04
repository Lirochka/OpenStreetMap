package com.example.openstreetmap.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Position(
    val image: Int,
    val title: String,
    val description: String,
    val price: String
): Parcelable
