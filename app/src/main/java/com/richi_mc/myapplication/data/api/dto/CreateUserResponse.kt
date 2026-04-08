package com.richi_mc.myapplication.data.api.dto

import com.google.gson.annotations.SerializedName

data class CreateUserResponse(
    @SerializedName("user_id") val userId: String,
)
