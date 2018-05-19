package com.krokyze.dodies.repository.api

sealed class NetworkRequest<T> {
    data class Progress<T>(var loading: Boolean) : NetworkRequest<T>()
    data class Success<T>(var data: T) : NetworkRequest<T>()
    data class Failure<T>(val e: Throwable) : NetworkRequest<T>()

    companion object {
        fun <T> loading(isLoading: Boolean): NetworkRequest<T> = Progress(isLoading)

        fun <T> success(data: T): NetworkRequest<T> = Success(data)

        fun <T> failure(e: Throwable): NetworkRequest<T> = Failure(e)
    }
}