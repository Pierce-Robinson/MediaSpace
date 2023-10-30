package com.varsitycollege.mediaspace.data

data class User(
    val id: String ?= null,
    val username: String ?= null,
    val email: String ?= null,
    val mobile: String ?= null,
    val deliveryAddresses: ArrayList<Delivery> ?= null,
    val orderHistory: ArrayList<Order> ?= null,
    val notifications: Boolean ?= null
)
