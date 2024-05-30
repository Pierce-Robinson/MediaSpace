package com.varsitycollege.mediaspaceadmin.data

import java.io.Serializable

data class Order(
    val orderNum: String ?= null,
    val date: String ?= null,
    var status: String ?= null,
    val customerId: String ?= null,
    val deliveryId: String ?= null,
    var customProductsList: ArrayList<CustomProduct> ?= null
): Serializable
