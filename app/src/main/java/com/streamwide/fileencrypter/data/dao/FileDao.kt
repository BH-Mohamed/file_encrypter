package com.streamwide.fileencrypter.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.streamwide.fileencrypter.data.entity.FileEntity
import kotlinx.coroutines.flow.Flow

interface FileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: FileEntity)

    @Delete
    suspend fun delete(file: FileEntity)

    @Query("SELECT * FROM File ORDER BY createdAt")
    fun getFiles(): Flow<MutableList<FileEntity>>

}