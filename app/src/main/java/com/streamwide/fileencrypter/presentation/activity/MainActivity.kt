package com.streamwide.fileencrypter.presentation.activity

import android.os.Bundle
import com.streamwide.fileencrypter.R
import com.streamwide.fileencrypter.presentation.base.BaseActivity
import com.streamwide.fileencrypter.presentation.commons.deleteDirectory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        //delete temp files when closing app
        deleteDirectory(java.io.File(getExternalFilesDir("temp")?.absolutePath ?: ""))
        super.onDestroy()
    }

}