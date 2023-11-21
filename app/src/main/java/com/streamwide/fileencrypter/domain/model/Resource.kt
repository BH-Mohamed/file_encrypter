package com.streamwide.fileencrypter.domain.model

sealed class Resource<T>(val data: T?, val message: String? ) {
    class Success<T>(data: T , message: String = "") : Resource<T>(data, message)
    class Loading<T> : Resource<T>(null , null)
    class Error<T>(message: String,val errors: HashMap<String, String>?=null) : Resource<T>(null, message)
}
