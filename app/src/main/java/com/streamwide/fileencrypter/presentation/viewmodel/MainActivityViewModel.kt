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

    //observe changes on listing files
    val filesObserver = MutableStateFlow<Resource<List<File>>?>(null)
    //observe saving and encrypt file
    val saveFileObserver = MutableStateFlow<Resource<Boolean>?>(null)
    //observe get file by id
    val fileDetailObserver = MutableStateFlow<Resource<File>?>(null)

    //used to show loading progress for multi file insertion
    var currentProcessingFile : Int =0
    /**
     * encrypt file and save it into DB
     */
    fun encryptFileAndSaveIt(context: Context, uri: Uri) {
        launchOnUI {
            // Notify observer that the file saving process is in progress
            saveFileObserver.value = Resource.Loading()
            updateCurrentProcessingData(1)


            try {
                // Retrieve the byte array representation of the file from the URI
                val fileByteArray = uri.toFileByteArray(context)!!
                // Encrypt the file byte array
                val encryptedFile = encryptor.encryptFile(fileByteArray)

                // Get the directory path for storing encrypted files
                val dirPath = context.getExternalFilesDir("secure")?.absoluteFile ?: ""
                // Generate a random file name
                val fileName = uri.fileName(context)

                // Construct the full path for the encrypted file
                val secureFilePath = "$dirPath/${randomString()}.txt"

                // Save the encrypted file to the specified path (the file saved with a random name)
                val encryptFile  = encryptor.saveFile(encryptedFile, secureFilePath)

                // Create a File object representing the encrypted file
                val file = File(
                    name = fileName,
                    extension = fileName.split('.')[1],
                    size = fileByteArray.sizeValue(),
                    createdAt = Calendar.getInstance(),
                    path = secureFilePath
                )

                // Attempt to add the encrypted file to the secure folder
                repository.addFileToSecureFolder(file).collect {
                    updateCurrentProcessingData(-1)

                    when (it) {
                        is Resource.Error -> {
                            //delete file if created
                            if(encryptFile.exists()) encryptFile.delete()
                            saveFileObserver.value =
                                Resource.Error(context.getString(R.string.error_insert_file))
                        }

                        is Resource.Success -> {
                            saveFileObserver.value = it
                        }

                        is Resource.Loading -> {}
                    }
                }

            }catch (e : MaxFileSizeException){
                e.printStackTrace()
                updateCurrentProcessingData(-1)
                saveFileObserver.value =
                    Resource.Error(e.message?:"")
            }
            catch (e: Exception) {
                e.printStackTrace()
                updateCurrentProcessingData(-1)
                saveFileObserver.value =
                    Resource.Error(context.getString(R.string.error_insert_file))
            }


        }

    }

    private fun updateCurrentProcessingData(processing : Int){
        currentProcessingFile+=processing
        if (currentProcessingFile<0)currentProcessingFile=0
    }

    /**
     * get list of files
     */
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