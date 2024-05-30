package com.varsitycollege.mediaspaceadmin.data

import java.io.Serializable

data class Colour(
    var colour: String ?= null,
    var name: String ?= null,
    var abbreviation: String ?= null,
    var available: Boolean ?= null
) : Serializable
