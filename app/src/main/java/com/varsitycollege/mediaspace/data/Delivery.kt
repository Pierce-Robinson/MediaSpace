package com.varsitycollege.mediaspace.data

import java.io.Serializable

data class Delivery(
    val id: String ?= null,
    var customerId: String ?= null,
    var postalCode: String ?= null,
    var addressLineOne: String ?= null,
    var addressLineTwo: String ?= null,
    var suburb: String ?= null,
    var city: String ?= null,
    var country: String ?= null
)    : Serializable
