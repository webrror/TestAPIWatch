package com.rejie.testapiwatch.domain.models

data class MainResponse(
    val limit: Int,
    val products: List<Product>,
    val skip: Int,
    val total: Int
)
