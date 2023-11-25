package com.streamwide.fileencrypter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.streamwide.fileencrypter.domain.model.File
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

/**mapping the entity from db into model**/
fun FileEntity.toFile() = File(
    id=id,
    name = name,
    size = size,
    path = encryptedPath,
    createdAt = Calendar.getInstance().also { it.timeInMillis=createdAt },
    extension = extension
)

/**mapping the model into entity**/
fun File.toFileEntity() = FileEntity(
    id=id,
    name = name,
    size = size,
    encryptedPath = path,
    createdAt = createdAt.timeInMillis,
    extension = extension

)