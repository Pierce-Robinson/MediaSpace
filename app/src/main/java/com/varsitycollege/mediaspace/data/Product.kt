package com.varsitycollege.mediaspace.data

data class Product(
    val id: String ?= null,
    val sku: String ?= null,
    val stock: Int ?= null,
    val available: Boolean ?= null,
    val name: String ?= null,
    val description: String ?= null,
    val price: Double ?= null,
    val colourList: ArrayList<Colour> ?= null,
    val sizeList: ArrayList<Size> ?= null,
    val categoriesList: ArrayList<String> ?= null,
    val imagesList: ArrayList<String> ?= null
)
