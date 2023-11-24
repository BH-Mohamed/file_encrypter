package com.streamwide.fileencrypter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.streamwide.fileencrypter.domain.model.File
import com.streamwide.fileencrypter.presentation.commons.formatDate
import java.util.Calendar

@Entity(tableName="File")
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name : String,
    val size : String,
    val encryptedPath : String,
    val createdAt : Long,
    val extension:String
)

//mapping
fun FileEntity.toFile() = File(
    id=id,
    name = name,
    size = size,
    path = encryptedPath,
    createdAt = Calendar.getInstance().also { it.timeInMillis=createdAt },
    extension = extension
)

fun File.toFileEntity() = FileEntity(
    id=id,
    name = name,
    size = size,
    encryptedPath = path,
    createdAt = createdAt.timeInMillis,
    extension = extension

)