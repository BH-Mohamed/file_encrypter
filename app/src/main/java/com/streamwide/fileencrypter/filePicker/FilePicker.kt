package com.streamwide.fileencrypter.filePicker

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

    lateinit var activity: Activity

    interface OnFilePickerListener {
        fun onFilePicked(uri: Uri)
        fun onFilePickerFailed()
    }

    private val requestPermissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        if(permissions.entries.all { it.value }){
            importFile(activity)
        }
    }

    private var resultFilePickerLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                try {
                    val fileUri = result!!.data!!.data!!
                    onImagePickedListener.onFilePicked(fileUri)
                }catch (e : Exception){
                    e.printStackTrace()
                    onImagePickedListener.onFilePickerFailed()
                }
            }

        }



    fun importFile(activity: Activity) {
        this.activity = activity
        if (checkPermission()) {
            var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.type = "*/*"
            chooseFile = Intent.createChooser(chooseFile, "Choose a file")
            resultFilePickerLauncher.launch(chooseFile)
        }

    }

    private fun checkPermission(): Boolean {

        val listPermissionNeeded: MutableList<String> = ArrayList()

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
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permissions
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                listPermissionNeeded.add(permissions)
            }
        }

        if (listPermissionNeeded.isNotEmpty()) {

            requestPermissionLauncher.launch(
                listPermissionNeeded.toTypedArray()
            )

            return false
        }
        return true
    }

}
