package com.varsitycollege.mediaspace.data

data class Order(
    val orderNum: String ?= null,
    val date: String ?= null,
    val status: String ?= null,
    val customerId: String ?= null,
    val deliveryId: String ?= null,
    val productsList: ArrayList<CustomProduct> ?= null
)
