package com.example.makerhub.common

sealed interface IResult<out T> {
    data class Success<T>(val data: T) : IResult<T>
    data class Error(
        val thr: Throwable,
        val errorCode: String? = null,
        val errorMessage: String? = null,
    ) : IResult<Nothing>
}