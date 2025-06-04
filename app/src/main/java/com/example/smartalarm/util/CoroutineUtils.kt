package com.example.smartalarm.util

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Utility functions for coroutines.
 */
object CoroutineUtils {
    
    /**
     * Creates a new coroutine and runs it in the specified context.
     * @param context The coroutine context to use
     * @param start The coroutine start option (default: CoroutineStart.DEFAULT)
     * @param block The coroutine code to execute
     * @return The created Job
     */
    fun launchInContext(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = CoroutineScope(Dispatchers.Main + context).launch(context, start, block)
    
    /**
     * Runs the block in the IO dispatcher.
     */
    suspend fun <T> io(block: suspend CoroutineScope.() -> T): T = withContext(Dispatchers.IO, block)
    
    /**
     * Runs the block in the Main dispatcher.
     */
    suspend fun <T> main(block: suspend CoroutineScope.() -> T): T = withContext(Dispatchers.Main, block)
    
    /**
     * Runs the block in the Default dispatcher.
     */
    suspend fun <T> default(block: suspend CoroutineScope.() -> T): T = withContext(Dispatchers.Default, block)
    
    /**
     * Creates a debounced version of the original flow that emits items only after a specified delay
     * has passed without another item being emitted by the original flow.
     *
     * @param timeoutMillis The timeout in milliseconds
     * @return A new flow that debounces emissions from the original flow
     */
    fun <T> Flow<T>.debounce(timeoutMillis: Long): Flow<T> = this.debounce(timeoutMillis)
    
    /**
     * Creates a throttled version of the original flow that emits the first item and then ignores
     * subsequent items for a specified duration.
     *
     * @param periodMillis The period in milliseconds to throttle by
     * @return A new flow that throttles emissions from the original flow
     */
    fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T> {
        require(periodMillis > 0) { "Period should be positive" }
        
        return flow {
            var lastTime = 0L
            collect { value ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTime >= periodMillis) {
                    lastTime = currentTime
                    emit(value)
                }
            }
        }
    }
    
    /**
     * Retries the given block with exponential backoff.
     *
     * @param times The number of times to retry
     * @param initialDelayMillis The initial delay in milliseconds
     * @param maxDelayMillis The maximum delay in milliseconds
     * @param factor The exponential factor to use for backoff
     * @param block The block to retry
     * @return The result of the block if it succeeds
     * @throws Exception The last exception if all retries fail
     */
    suspend fun <T> retryWithBackoff(
        times: Int = 3,
        initialDelayMillis: Long = 100,
        maxDelayMillis: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMillis
        repeat(times - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                if (attempt == times - 1) throw e
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
            }
        }
        return block() // last attempt
    }
    
    /**
     * A helper function to wrap a suspend function in a try-catch block and return a Result.
     */
    suspend fun <T> tryCatch(block: suspend () -> T): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * A helper function to wrap a suspend function in a try-catch block and return a Result with a default value on failure.
     */
    suspend fun <T> tryCatch(defaultValue: T, block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            defaultValue
        }
    }
    
    /**
     * A helper function to wrap a suspend function in a try-catch block and return a nullable result.
     */
    suspend fun <T> tryCatchNull(block: suspend () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * A helper function to wrap a suspend function in a try-catch block and return a Result with a default value on failure.
     */
    suspend fun <T> tryCatch(
        onError: (Throwable) -> Unit = {},
        block: suspend () -> T
    ): T? {
        return try {
            block()
        } catch (e: Exception) {
            onError(e)
            null
        }
    }
}
