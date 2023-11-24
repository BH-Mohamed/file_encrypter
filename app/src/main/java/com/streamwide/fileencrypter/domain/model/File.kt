package com.streamwide.fileencrypter.domain.model

import com.streamwide.fileencrypter.presentation.commons.formatDate
import java.util.Calendar

data class File(
    val id : Int=0,
    val name : String,
    val size : String,
    val createdAt : Calendar,
    val path: String,
    val extension : String
){
    fun formatDate() = createdAt.formatDate()
}