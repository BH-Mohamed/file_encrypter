package com.streamwide.fileencrypter.domain.model

data class File(
    val id : Int=0,
    val name : String,
    val size : String,
    val createdAt : String,
    val path: String,
    val extension : String
)