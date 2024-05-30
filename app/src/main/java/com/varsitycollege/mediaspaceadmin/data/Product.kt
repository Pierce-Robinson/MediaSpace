package com.varsitycollege.mediaspaceadmin.data

data class Product(
    val id: String ?= null,
    var sku: String ?= null,
    var stock: Int ?= null,
    val available: Boolean ?= null,
    var name: String ?= null,
    var description: String ?= null,
    var price: Double ?= null,
    var colourList: ArrayList<Colour> ?= null,
    val sizeList: ArrayList<Size> ?= null,
    var categoriesList: ArrayList<String> ?= null,
    val imagesList: ArrayList<String> ?= null
)
