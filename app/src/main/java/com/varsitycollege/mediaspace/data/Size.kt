package com.varsitycollege.mediaspace.data

import java.io.Serializable

data class Size(
    val size: String ?= null,
    val available: Boolean ?= null
)    : Serializable
