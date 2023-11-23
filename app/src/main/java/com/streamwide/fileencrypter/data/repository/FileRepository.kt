package com.streamwide.fileencrypter.data.repository

import android.content.Context
import com.streamwide.fileencrypter.R
import com.streamwide.fileencrypter.domain.repository.IFileRepository
import com.streamwide.fileencrypter.data.dao.FileDao
import com.streamwide.fileencrypter.data.entity.toFile
import com.streamwide.fileencrypter.data.entity.toFileEntity
import com.streamwide.fileencrypter.domain.model.File
import com.streamwide.fileencrypter.domain.model.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*

class FileRepository(
    @ApplicationContext var context: Context,
    private val fileDao: FileDao
) : IFileRepository {

    override suspend fun addFileToSecureFolder(file: File)= flow {
        try {
            fileDao.insert(file.toFileEntity())
            emit(Resource.Success(true))
        }catch (e : Exception){
            e.printStackTrace()
            emit(Resource.Error(context.getString(R.string.error_insert_file)))

        }

    }

    override suspend fun getFiles() = flow {
        try {
            fileDao.getFiles().collect{
                emit(Resource.Success(it.map { it.toFile() }.sortedByDescending { it.createdAt }.toList()))
            }
        }catch (e : Exception){
            e.printStackTrace()
            emit(Resource.Error(context.getString(R.string.error_fetch_file)))
        }
    }

    override suspend fun removeFile(file: File) =flow<Resource<Boolean>> {
        try {
            fileDao.delete(file.toFileEntity())
            emit(Resource.Success(true))
        }catch (e : Exception){
            e.printStackTrace()
            emit(Resource.Error(context.getString(R.string.error_deleting_file)))
        }
    }

}