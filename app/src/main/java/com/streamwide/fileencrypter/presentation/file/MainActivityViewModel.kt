package com.streamwide.fileencrypter.presentation.file

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
    fun encryptFileAndSaveIt(context: Context, uri: Uri) {
        launchOnUI {
            saveFileObserver.value = Resource.Loading()

            try {
                val fileByteArray = uri.toFileByteArray(context)
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
                    createdAt = Calendar.getInstance().formatDate(),
                    path = secureFilePath
                )

                repository.addFileToSecureFolder(file).collect{
                    when(it){
                        is Resource.Error -> {
                            //delete file if created
                        }
                        is Resource.Success -> {
                            saveFileObserver.value = it
                        }
                        is Resource.Loading -> {}
                    }
                }

            }catch (e:Exception){
                e.printStackTrace()
                saveFileObserver.value = Resource.Error(context.getString(R.string.error_insert_file))
            }


        }

    }

    fun getFilesList(){

        launchOnUI {
            filesObserver.value = Resource.Loading()

            repository.getFiles().collect{
                filesObserver.value = it
            }
        }
    }
}