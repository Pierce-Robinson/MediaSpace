package com.varsitycollege.mediaspace.data



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
    val design: ArrayList<String> ?= null
)
//add custom image to cart
//display only first image from product
//display user custom instructions not the