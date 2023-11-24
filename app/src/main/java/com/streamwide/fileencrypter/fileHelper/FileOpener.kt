package com.streamwide.fileencrypter.fileHelper

import android.app.Activity
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.util.Locale


class FileOpener(
    fragment: Fragment,
) {

    private var file: File?=null

    private var resultFileOpenerLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                // if file exists in storage
                file?.also {
                    if (it.exists()) it.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    fun openFileFromSystem(activity:  Activity,file : File) {
        //open temp file on system
        //note : all temp file will be deleted in onDestroy activity
        val intent = Intent(Intent.ACTION_VIEW)
        val fileProviderPath = FileProvider.getUriForFile(
            activity,
            activity.packageName + ".fileprovider",
            file
        )

        this.file = file
        intent.setDataAndType(fileProviderPath, MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension.lowercase(
            Locale.ROOT)))
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        resultFileOpenerLauncher.launch(intent)


    }
}