package com.rejie.testapiwatch.domain.data

import com.rejie.testapiwatch.domain.models.Product
import kotlinx.coroutines.flow.Flow

interface ProductsRepository {
    suspend fun getProductsList(): Flow<Result<List<Product>>>
}