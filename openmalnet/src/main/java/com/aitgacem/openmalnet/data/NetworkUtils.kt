package com.aitgacem.openmalnet.data

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.network.http.HttpInfo
import openmal.domain.ApiError
import openmal.domain.NetworkResult
import retrofit2.HttpException
import retrofit2.Response

fun Int.toErrorEnum(): ApiError {
    return when (this) {
        400 -> ApiError.BAD_REQUEST
        401 -> ApiError.UNAUTHORIZED
        403 -> ApiError.FORBIDDEN
        404 -> ApiError.NOT_FOUND
        else -> ApiError.UNKNOWN
    }
}

suspend fun <T : Any> handleApi(
    execute: suspend () -> Response<T>
): NetworkResult<T> {
    return try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            NetworkResult.Success(body)
        } else {
            NetworkResult.Error(code = response.code(), apiError = response.code().toErrorEnum())
        }
    } catch (e: HttpException) {
        e.printStackTrace()
        NetworkResult.Error(code = e.code(), apiError = e.code().toErrorEnum())
    } catch (e: Throwable) {
        e.printStackTrace()
        NetworkResult.Exception(e)
    }
}

suspend fun <T : Operation.Data> handleGraphQlApi(
    execute: suspend () -> ApolloResponse<T>
): NetworkResult<T> {
    return try {
        val response = execute()
        val data = response.data
        val errors = response.errors
        val exception = response.exception
        if (exception != null && data == null && errors == null) {
            return NetworkResult.Exception(exception)
        }
        if (data != null && exception == null && errors == null) {
            return NetworkResult.Success(data)
        }
        return NetworkResult.Error(
            response.executionContext[HttpInfo]?.statusCode ?: 418, ApiError.UNKNOWN
        )
    } catch (e: HttpException) {
        e.printStackTrace()
        NetworkResult.Error(code = e.code(), apiError = e.code().toErrorEnum())
    } catch (e: Throwable) {
        e.printStackTrace()
        NetworkResult.Exception(e)
    }
}