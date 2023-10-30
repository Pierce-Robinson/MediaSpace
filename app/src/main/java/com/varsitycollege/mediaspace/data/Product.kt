package com.varsitycollege.mediaspace.data

data class Product(
    val sku: String ?= null,
    val stock: Int ?= null,
    val available: Boolean ?= null,
    val name: String ?= null,
    val description: String ?= null,
    val price: Double ?= null,
    val colour: String ?= null,
    val size: String ?= null,
    val categoriesList: ArrayList<String> ?= null,
    val imagesList: ArrayList<String> ?= null
)
