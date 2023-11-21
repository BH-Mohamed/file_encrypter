package com.streamwide.fileencrypter.domain.model

data class File(
    val id : Int,
    val name : String,
    val size : Int,
    val sizeUnit : String,
    val createdAt : String,
    val path: String
){
    fun getSizeWithUnit() = "$size$sizeUnit"
}