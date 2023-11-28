package com.varsitycollege.mediaspace.data

import java.io.Serializable

data class Colour(
    val colour: String ?= null,
    val name: String ?= null,
    val abbreviation: String ?= null,
    val available: Boolean ?= null
)    :Serializable

