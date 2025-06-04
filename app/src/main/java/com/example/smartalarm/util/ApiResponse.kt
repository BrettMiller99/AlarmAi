package com.example.smartalarm.util

import retrofit2.HttpException
import java.io.IOException

/**
 * A generic class that holds a value with its status.
 */
sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>() {
        override fun toString(): String = "[Success: data=$data]"
    }
    
    data class Error(
        val error: String,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : ApiResponse<Nothing>() {
        override fun toString(): String = "[Error: error=$error, code=$code]"
    }
    
    object Loading : ApiResponse<Nothing>() {
        override fun toString(): String = "[Loading]"
    }
    
    companion object {
        /**
         * Creates an [ApiResponse] with the given [data] as [ApiResponse.Success].
         */
        fun <T> success(data: T): ApiResponse<T> = Success(data)
        
        /**
         * Creates an [ApiResponse] with the given [error] as [ApiResponse.Error].
         */
        fun <T> error(error: String, code: Int? = null, throwable: Throwable? = null): ApiResponse<T> =
            Error(error, code, throwable)
            
        /**
         * Creates an [ApiResponse] from a Retrofit [HttpException].
         */
        fun <T> fromHttpException(exception: HttpException): ApiResponse<T> {
            return try {
                val errorBody = exception.response()?.errorBody()?.string()
                val errorMsg = errorBody ?: exception.message()
                error(errorMsg, exception.code(), exception)
            } catch (e: Exception) {
                error(exception.message ?: "Unknown error", exception.code(), exception)
            }
        }
        
        /**
         * Creates an [ApiResponse] from a [Throwable].
         */
        fun <T> fromThrowable(throwable: Throwable): ApiResponse<T> {
            return when (throwable) {
                is HttpException -> fromHttpException(throwable)
                is IOException -> error("Network error: ${throwable.message}", null, throwable)
                else -> error("Unexpected error: ${throwable.message}", null, throwable)
            }
        }
        
        /**
         * Creates a loading [ApiResponse].
         */
        fun <T> loading(): ApiResponse<T> = Loading
    }
}

/**
 * Converts a [Result] to an [ApiResponse].
 */
fun <T> Result<T>.toApiResponse(): ApiResponse<T> {
    return when (this) {
        is Result.Success -> ApiResponse.success(data)
        is Result.Error -> ApiResponse.fromThrowable(exception)
        is Result.Loading -> ApiResponse.loading()
    }
}

/**
 * Maps the success value of an [ApiResponse] using the given [transform] function.
 */
fun <T, R> ApiResponse<T>.map(transform: (T) -> R): ApiResponse<R> {
    return when (this) {
        is ApiResponse.Success -> ApiResponse.Success(transform(data))
        is ApiResponse.Error -> this
        is ApiResponse.Loading -> this
    }
}

/**
 * Executes the given [action] if this [ApiResponse] is a success.
 */
inline fun <T> ApiResponse<T>.onSuccess(action: (T) -> Unit): ApiResponse<T> {
    if (this is ApiResponse.Success) {
        action(data)
    }
    return this
}

/**
 * Executes the given [action] if this [ApiResponse] is an error.
 */
inline fun <T> ApiResponse<T>.onError(action: (String, Int?) -> Unit): ApiResponse<T> {
    if (this is ApiResponse.Error) {
        action(error, code)
    }
    return this
}

/**
 * Executes the given [action] if this [ApiResponse] is loading.
 */
inline fun <T> ApiResponse<T>.onLoading(action: () -> Unit): ApiResponse<T> {
    if (this is ApiResponse.Loading) {
        action()
    }
    return this
}
