package com.varsitycollege.mediaspace.data

import java.io.Serializable


data class CustomProduct (
    val userId: String ?= null,
    val sku: String ?= null,
    val prodName: String ?= null,
    val price: Double ?= null,
    val quantity: Int ?= null,
    val userInstructions: String ?= null,
    val colour: Colour ?= null,
    val size: String ?= null,
    //Design: the link to the user's uploaded design image in firebase
    var design: ArrayList<String> ?= null,
    val firstImage: String?= null
): Serializable
