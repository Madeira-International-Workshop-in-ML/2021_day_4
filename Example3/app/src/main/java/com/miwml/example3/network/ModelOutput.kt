package com.miwml.example3.network

import com.squareup.moshi.Json

data class ModelOutput(
    @Json(name = "input") val input: String,
    @Json(name = "output") val output: String
)