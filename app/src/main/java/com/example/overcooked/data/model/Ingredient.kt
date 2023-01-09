package com.example.overcooked.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    var name : String = "",
    var massInGram : Double = 0.0
) : Parcelable
