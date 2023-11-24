package com.varsitycollege.mediaspace.data

import java.io.Serializable

data class User(
    val id: String ?= null,
    val title: String ?= null,
    val firstName: String ?= null,
    val lastName: String ?= null,
    val email: String ?= null,
    val mobile: String ?= null,
    val deliveryAddresses: ArrayList<Delivery> ?= null,
    val orderHistory: ArrayList<Order> ?= null,
    val notifications: Boolean ?= null
) : Serializable
