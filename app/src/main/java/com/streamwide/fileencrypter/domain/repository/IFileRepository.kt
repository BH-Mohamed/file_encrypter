package com.streamwide.fileencrypter.domain.repository

import com.streamwide.fileencrypter.domain.model.File
import com.streamwide.fileencrypter.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface IFileRepository {

   suspend fun addFileToSecureFolder(file: File) : Flow<Resource<Boolean>>

   suspend fun getFiles() : Flow<Resource<List<File>>>

   suspend fun removeFile(file: File) : Flow<Resource<Boolean>>

   suspend fun getFileById(fileId: Int) : Flow<Resource<File>>

}