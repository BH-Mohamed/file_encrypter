package com.streamwide.domain.repository

import com.streamwide.domain.model.File
import com.streamwide.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface IFileRepository {

   suspend fun addFileToSecureFolder(file: File) : Flow<Resource<Boolean>>

   suspend fun getFiles() : Flow<Resource<List<File>>>

   suspend fun removeFile(file: File) : Flow<Resource<Boolean>>


}