package com.streamwide.fileencrypter.presentation.file

import android.os.Bundle
import android.util.Log
import android.view.View
import com.streamwide.fileencrypter.R
import com.streamwide.fileencrypter.presentation.base.BaseActivity
import com.streamwide.fileencrypter.presentation.commons.deleteDirectory
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
    }
}