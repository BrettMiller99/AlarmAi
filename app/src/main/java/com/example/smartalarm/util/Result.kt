package com.example.smartalarm.util

/**
 * A generic class that holds a value with its loading status.
 * @param T The type of the data.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>() {
        override fun toString(): String = "[Success: data=$data]"
    }
    
    data class Error(val exception: Throwable) : Result<Nothing>() {
        override fun toString(): String = "[Error: exception=$exception]"
    }
    
    object Loading : Result<Nothing>() {
        override fun toString(): String = "[Loading]"
    }
    
    // Add an isSuccess property for easy checking
    val isSuccess: Boolean
        get() = this is Success<T>
    
    // Add an isError property for easy checking
    val isError: Boolean
        get() = this is Error
    
    // Add an isLoading property for easy checking
    val isLoading: Boolean
        get() = this is Loading
    
    // Get the success data or null if not Success
    fun getOrNull(): T? = (this as? Success<T>)?.data
    
    // Get the error or null if not Error
    fun exceptionOrNull(): Throwable? = (this as? Error)?.exception
    
    companion object {
        /**
         * Creates a [Result] with the given [data] as [Result.Success].
         */
        fun <T> success(data: T): Result<T> = Success(data)
        
        /**
         * Creates a [Result] with the given [exception] as [Result.Error].
         */
        fun <T> error(exception: Throwable): Result<T> = Error(exception)
        
        /**
         * Creates a [Result] with [Result.Loading] state.
         */
        fun <T> loading(): Result<T> = Loading
    }
}

/**
 * Maps the result of a [Result] using the given [transform] function if the result is a success.
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
        is Result.Loading -> this
    }
}

/**
 * Maps the error of a [Result] using the given [transform] function if the result is an error.
 */
inline fun <T> Result<T>.mapError(transform: (Throwable) -> Throwable): Result<T> {
    return when (this) {
        is Result.Success -> this
        is Result.Error -> Result.Error(transform(exception))
        is Result.Loading -> this
    }
}

/**
 * Executes the given [action] if the result is a success.
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

/**
 * Executes the given [action] if the result is an error.
 */
inline fun <T> Result<T>.onError(action: (Throwable) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}

/**
 * Executes the given [action] if the result is loading.
 */
inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}
