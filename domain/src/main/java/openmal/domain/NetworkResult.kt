package openmal.domain


sealed class NetworkResult<T : Any> {
    /**
     * Represents a network result that successfully received a response containing body data
     */
    class Success<T : Any>(val data: T) : NetworkResult<T>()

    /**
     * Represents a network result that successfully received a response containing an error message.
     */
    class Error<T : Any>(val code: Int, val apiError: ApiError) : NetworkResult<T>()

    /**
     * Represents a network result that faced an unexpected exception before getting a response from the network
     */
    class Exception<T : Any>(val e: Throwable) : NetworkResult<T>()
}