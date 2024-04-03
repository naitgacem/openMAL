package com.aitgacem.openmalnet.data

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