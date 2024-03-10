package com.rejie.testapiwatch.domain.data

import com.rejie.testapiwatch.domain.models.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class ProductsRepositoryImpl(
    private val api:Api
): ProductsRepository {
    override suspend fun getProductsList(): Flow<Result<List<Product>>> {
        return  flow {
            val productsFromApi = try {
                api.getProductsList()
            } catch (e: IOException){
                e.printStackTrace()
                emit(Result.Error(message = "Something went wrong"))
                return@flow
            } catch (e: HttpException){
                e.printStackTrace()
                emit(Result.Error(message = "Something went wrong"))
                return@flow
            } catch (e: Exception){
                e.printStackTrace()
                emit(Result.Error(message = "Something went wrong"))
                return@flow
            }
            emit(Result.Success(data = productsFromApi.products))
        }
    }
}