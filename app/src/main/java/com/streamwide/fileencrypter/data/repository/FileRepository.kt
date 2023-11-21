package com.streamwide.fileencrypter.data.repository

import com.streamwide.domain.repository.IFileRepository
import com.streamwide.fileencrypter.data.dao.FileDao
import com.streamwide.fileencrypter.data.entity.toFile
import com.streamwide.fileencrypter.data.entity.toFileEntity
import com.streamwide.fileencrypter.domain.model.File
import com.streamwide.fileencrypter.domain.model.Resource
import kotlinx.coroutines.flow.*

class FileRepository(private val fileDao: FileDao) : IFileRepository  {

    override suspend fun addFileToSecureFolder(file: File)= flow {
        emit(Resource.Loading())
        fileDao.insert(file.toFileEntity())
        emit(Resource.Success(true))

    }

    override suspend fun getFiles() = channelFlow {
        trySend(Resource.Loading())
        trySend(Resource.Success(fileDao.getFiles().last().map { it.toFile() }.toList()))

    }

    override suspend fun removeFile(file: File) =flow<Resource<Boolean>> {
        emit(Resource.Loading())
        fileDao.delete(file.toFileEntity())
        emit(Resource.Success(true))
    }

}