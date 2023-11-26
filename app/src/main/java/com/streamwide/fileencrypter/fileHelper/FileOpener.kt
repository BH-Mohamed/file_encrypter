package com.streamwide.fileencrypter.fileHelper

import android.app.Activity
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.util.Locale

/**
 * class used to open file via system
 */
class FileOpener(
    fragment: Fragment,
) {

    // File reference to track the opened file
    private var file: File?=null

    // Activity result launcher used to delete the file when it's closed
    private var resultFileOpenerLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            try {
                // Delete the file if it exists in storage
                file?.also {
                    if (it.exists()) it.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
    }

    /**
     * Opens a file from the system using an Intent.
     *
     * @param activity The hosting activity.
     * @param file The file to be opened.
     */
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
        // Set data and type for the intent
        intent.setDataAndType(fileProviderPath, MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension.lowercase(
            Locale.ROOT)))

        // Add read URI permission flag
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            file.deleteOnExit()
        }catch (e : Exception){e.printStackTrace()}
        // Launch the file opener intent
        resultFileOpenerLauncher.launch(intent)


    }
}