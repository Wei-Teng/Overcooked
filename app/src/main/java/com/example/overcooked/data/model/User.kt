package com.example.overcooked.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class User(
    var email : String = "",
    var password : String = "",
    var region : String = "",
    var username : String = "",
    var image : String = "",
    var bio : String = "No bio",
    var followerNum : Int = 0,
    var followingNum : Int = 0,
    var followingUserIdList : ArrayList<String> = arrayListOf(),
    var followerUserIdList : ArrayList<String> = arrayListOf(),
    var id : String = UUID.randomUUID().toString(),
    var firebaseAuthId: String = ""
) : Parcelable
