package com.rooze.insta_2.domain.common

sealed class DataResult<T> {
    class Fail<T>(val message: String) : DataResult<T>()
    class Success<T>(val data: T) : DataResult<T>()
}