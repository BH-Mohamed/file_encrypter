package com.streamwide.fileencrypter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.streamwide.fileencrypter.domain.model.File

@Entity(tableName="File")
data class FileEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name : String,
    val size : String,
    val encryptedPath : String,
    val createdAt : String,
    val extension:String
)

//mapping
fun FileEntity.toFile() = File(
    id=id,
    name = name,
    size = size,
    path = encryptedPath,
    createdAt = createdAt,
    extension = extension
)

fun File.toFileEntity() = FileEntity(
    id=id,
    name = name,
    size = size,
    encryptedPath = path,
    createdAt = createdAt,
    extension = extension

)