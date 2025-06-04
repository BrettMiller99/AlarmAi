package com.example.smartalarm.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit

/**
 * A generic class that can provide a resource backed by both the SQLite database and the network.
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param ResultType - Type for the Resource data
 * @param RequestType - Type for the API response
 */
abstract class NetworkBoundResource<ResultType, RequestType> {
    
    private val result: Flow<Resource<ResultType>> = flow {
        // Emit loading state
        emit(Resource.loading(null))
        
        try {
            // First, check if we should fetch from network
            if (shouldFetch(fetchFromLocal().first())) {
                // If we need to fetch from network, emit loading state with current data
                emit(Resource.loading(fetchFromLocal().first()))
                
                // Fetch from network
                val apiResponse = fetchFromNetwork()
                
                // Save the response to the database
                apiResponse.onSuccess { response ->
                    // Save the response to the database
                    saveNetworkResult(response)
                    
                    // Emit the new data from the database
                    fetchFromLocal().collect { localData ->
                        emit(Resource.success(localData))
                    }
                }.onError { exception ->
                    // If network request fails, emit the error with the current data
                    fetchFromLocal().collect { localData ->
                        emit(
                            Resource.error(
                                exception.message ?: "Unknown error",
                                localData
                            )
                        )
                    }
                }
            } else {
                // If we don't need to fetch from network, just emit the local data
                fetchFromLocal().collect { localData ->
                    emit(Resource.success(localData))
                }
            }
        } catch (e: Exception) {
            // If any exception occurs, emit the error with the current data
            emit(Resource.error(e.message ?: "Unknown error", null))
        }
    }
    
    /**
     * Convert the API response to the database model and save it to the database.
     */
    @Throws(Exception::class)
    protected abstract suspend fun saveNetworkResult(item: RequestType)
    
    /**
     * Returns a [Flow] that fetches the data from the database.
     */
    protected abstract fun fetchFromLocal(): Flow<ResultType>
    
    /**
     * Returns a [Result] that fetches the data from the network.
     */
    protected abstract suspend fun fetchFromNetwork(): Result<RequestType>
    
    /**
     * Returns true if the data should be fetched from the network.
     * @param data The current data in the database
     */
    protected open fun shouldFetch(data: ResultType?): Boolean = true
    
    /**
     * Returns a [Flow] that represents the resource, which follows the following sequence:
     * 1. Emit [Resource.loading] with the current data (if any)
     * 2. Emit [Resource.success] with the data from the local database if it exists
     * 3. Emit [Resource.error] if the network request fails
     */
    fun asFlow(): Flow<Resource<ResultType>> = result
}

/**
 * A generic class that holds a value with its loading status.
 * @param status The status of the data (loading, success, error)
 * @param data The data if the request was successful
 * @param message The error message if the request failed
 */
data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }
        
        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }
        
        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
    
    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }
}

/**
 * Converts a [Result] to a [Resource].
 */
fun <T> Result<T>.toResource(): Resource<T> {
    return when (this) {
        is Result.Success -> Resource.success(data)
        is Result.Error -> Resource.error(exception.message ?: "Unknown error", null)
        is Result.Loading -> Resource.loading(null)
    }
}

/**
 * A helper function to create a [NetworkBoundResource] with a lambda for the network call.
 */
fun <ResultType, RequestType> networkBoundResource(
    query: () -> Flow<ResultType>,
    fetch: suspend () -> Result<RequestType>,
    saveFetchResult: suspend (RequestType) -> Unit,
    shouldFetch: (ResultType?) -> Boolean = { true }
): Flow<Resource<ResultType>> = flow {
    // Emit loading state with current data
    val data = query().first()
    emit(Resource.loading(data))
    
    try {
        // Check if we should fetch from network
        if (shouldFetch(data)) {
            // Fetch from network
            val response = fetch()
            
            response.onSuccess { result ->
                // Save the result to the database
                saveFetchResult(result)
                
                // Emit the new data from the database
                query().collect { localData ->
                    emit(Resource.success(localData))
                }
            }.onError { exception ->
                // If network request fails, emit the error with the current data
                emit(Resource.error(exception.message ?: "Unknown error", data))
            }
        } else {
            // If we don't need to fetch from network, just emit the local data
            query().collect { localData ->
                emit(Resource.success(localData))
            }
        }
    } catch (e: Exception) {
        // If any exception occurs, emit the error with the current data
        emit(Resource.error(e.message ?: "Unknown error", data))
    }
}
