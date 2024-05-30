package com.varsitycollege.mediaspaceadmin.data

import java.io.Serializable

//implements serializable for passing data through intents
//https://stackoverflow.com/questions/23142893/parcelable-encountered-ioexception-writing-serializable-object-getactivity
//accessed 27 November 2023
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
) : Serializable
