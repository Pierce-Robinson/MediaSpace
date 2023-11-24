package com.varsitycollege.mediaspace.data

import java.io.Serializable

data class Delivery(
    val id: String ?= null,
    val customerId: String ?= null,
    val addressLineOne: String ?= null,
    val addressLineTwo: String ?= null,
    val suburb: String ?= null,
    val city: String ?= null,
    val country: String ?= null,
    val province: String ?= null
)    : Serializable
