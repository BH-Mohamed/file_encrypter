package com.streamwide.fileencrypter.domain.repository

import com.streamwide.fileencrypter.domain.model.File
import com.streamwide.fileencrypter.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface IFileRepository {

   /**
    * save file into db
    */
   suspend fun addFileToSecureFolder(file: File) : Flow<Resource<Boolean>>

   /**
    * get list of encrypted files
    */
   suspend fun getFiles() : Flow<Resource<List<File>>>

   /**
    * remove encrypted file from db
    */
   suspend fun removeFile(file: File) : Flow<Resource<Boolean>>

   /**
    * get encrypted file by id
    */
   suspend fun getFileById(fileId: Int) : Flow<Resource<File>>

}