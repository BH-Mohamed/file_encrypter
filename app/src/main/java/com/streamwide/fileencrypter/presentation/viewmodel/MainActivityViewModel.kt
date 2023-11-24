package com.streamwide.fileencrypter.presentation.viewmodel

import android.content.Context
import android.net.Uri
import com.streamwide.fileencrypter.R
import com.streamwide.fileencrypter.domain.model.File
import com.streamwide.fileencrypter.domain.model.Resource
import com.streamwide.fileencrypter.domain.repository.IFileRepository
import com.streamwide.fileencrypter.encrypter.EncryptionFile
import com.streamwide.fileencrypter.presentation.base.BaseViewModel
import com.streamwide.fileencrypter.presentation.commons.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: IFileRepository,
    private val encryptor: EncryptionFile
) : BaseViewModel() {

    val filesObserver = MutableStateFlow<Resource<List<File>>?>(null)
    val saveFileObserver = MutableStateFlow<Resource<Boolean>?>(null)
    val fileDetailObserver = MutableStateFlow<Resource<File>?>(null)

    fun encryptFileAndSaveIt(context: Context, uri: Uri) {
        launchOnUI {
            saveFileObserver.value = Resource.Loading()

            try {
                val fileByteArray = uri.toFileByteArray(context)!!
                val encryptedFile = encryptor.encryptFile(fileByteArray)

                //get directory file
                val dirPath = context.getExternalFilesDir("secure")?.absoluteFile ?: ""
                val fileName = uri.fileName(context)

                val secureFilePath = "$dirPath/${randomString()}.txt"
                encryptor.saveFile(encryptedFile, secureFilePath)

                val file = File(
                    name = fileName,
                    extension = fileName.split('.')[1],
                    size = fileByteArray.sizeValue(),
                    createdAt = Calendar.getInstance(),
                    path = secureFilePath
                )

                repository.addFileToSecureFolder(file).collect {
                    when (it) {
                        is Resource.Error -> {
                            //delete file if created
                        }

                        is Resource.Success -> {
                            saveFileObserver.value = it
                        }

                        is Resource.Loading -> {}
                    }
                }

            }catch (e : MaxFileSizeException){
                e.printStackTrace()
                saveFileObserver.value =
                    Resource.Error(e.message?:"")
            }
            catch (e: Exception) {
                e.printStackTrace()
                saveFileObserver.value =
                    Resource.Error(context.getString(R.string.error_insert_file))
            }


        }

    }

    fun getFilesList() {

        launchOnUI {
            filesObserver.value = Resource.Loading()

            repository.getFiles().collect {
                filesObserver.value = it
            }
        }
    }

    fun deleteFile(file: File) {

        launchOnUI {
            filesObserver.value = Resource.Loading()

            //delete file from storage
            java.io.File(file.path).also {
                if (it.exists()) it.delete()
            }
            //delete file from data base
            repository.removeFile(file).collect {}
        }
    }

    private var decryptedFileCoroutineScope: CoroutineScope? = null
    fun decryptFile(
        context: Context,
        file: File,
        onDecrypted: (decryptedFile: java.io.File) -> Unit
    ) =
        launchOnUI {
            decryptedFileCoroutineScope = this
            //decrypt file
            val decryptedByteArray =
                encryptor.decryptEncryptedFile(java.io.File(file.path))
            //save decrypted file into temp dir
            val decryptedFile = encryptor.saveFile(
                decryptedByteArray,
                (context.getExternalFilesDir("temp")?.absolutePath ?: "") + "/${file.name}"
            )

            //check if coroutine cancelled
            if (decryptedFileCoroutineScope != null) {
                onDecrypted(decryptedFile)
            } else {
                decryptedFile.also {if (it.exists()) it.delete() }
            }
        }


    fun cancelDecryptingFile() {
        decryptedFileCoroutineScope?.cancel()
        decryptedFileCoroutineScope = null
    }

    fun getFileById(id : Int) {

        launchOnUI {
            filesObserver.value = Resource.Loading()

            repository.getFileById(id).collect {
                fileDetailObserver.value = it
            }
        }
    }
}