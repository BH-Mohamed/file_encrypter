package com.streamwide.fileencrypter.fileHelper

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*

class FilePicker(
      fragment: Fragment,
      onImagePickedListener: OnFilePickerListener
) {

    private lateinit var activity: Activity

    // Interface to communicate file picking events
    interface OnFilePickerListener {
        fun onFilePicked(uri: Uri)
        fun onFilePickerFailed()
    }

    // Activity result launcher for multiple permissions
    private val requestPermissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        if(permissions.entries.all { it.value }){
            // All required permissions are granted, proceed to pick a file
            pickFile(activity)
        }
    }

    // Activity result launcher for file picking
    private var resultFilePickerLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                try {
                    val fileUri = result!!.data!!.data!!
                    // Notify the listener that a file has been picked
                    onImagePickedListener.onFilePicked(fileUri)
                }catch (e : Exception){
                    e.printStackTrace()
                    // Notify the listener about the file picking failure
                    onImagePickedListener.onFilePickerFailed()
                }
            }

        }

    /**
     *Function to initiate the file picking process
     */
    fun pickFile(activity: Activity) {
        this.activity = activity

        // Check for permissions before launching the file picker
        if (checkPermission()) {
            var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.type = "*/*"
            chooseFile = Intent.createChooser(chooseFile, "Choose a file")
            // Launch the file picker intent
            resultFilePickerLauncher.launch(chooseFile)
        }

    }

    /**
     * Function to check and request permissions

     */
    private fun checkPermission(): Boolean {

        val listPermissionNeeded: MutableList<String> = ArrayList()

        // Add necessary permissions based on Android version
        arrayListOf<String>().also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.add(Manifest.permission.READ_MEDIA_VIDEO)
                it.add(Manifest.permission.READ_MEDIA_IMAGES)
                it.add(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                it.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    it.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }.forEach { permissions ->
            // Check if each permission is granted
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permissions
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionNeeded.add(permissions)
            }
        }

        // Request permissions if needed
        if (listPermissionNeeded.isNotEmpty()) {

            requestPermissionLauncher.launch(
                listPermissionNeeded.toTypedArray()
            )

            return false
        }

        // Permissions are granted
        return true
    }

}
