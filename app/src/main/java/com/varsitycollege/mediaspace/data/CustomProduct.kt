package com.varsitycollege.mediaspace.data

data class CustomProduct (
    val id: String ?= null,
    val price: Double ?= null,
    val quantity: Int ?= null,
    //Design: the link to the user's uploaded design image in firebase
    val design: String ?= null
)