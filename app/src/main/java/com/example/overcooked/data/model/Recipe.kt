package com.example.overcooked.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Recipe(
    var image : String = "",
    var title : String = "",
    var ingredientList : ArrayList<Ingredient> = arrayListOf(),
    var instruction : String = "",
    var servesNum : Int = 0,
    var cookTimeInMin : Double = 0.0,
    var authorId : String = "",
    var id : String = UUID.randomUUID().toString(),
    var rating : Double = 0.0,
    var reviewNum : Int = 0,
    var raterIdList : ArrayList<String> = arrayListOf(),
    var savedUserIdList : ArrayList<String> = arrayListOf()
) : Parcelable
