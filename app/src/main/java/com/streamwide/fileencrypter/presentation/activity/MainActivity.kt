package com.streamwide.fileencrypter.presentation.activity

import android.os.Bundle
import com.streamwide.fileencrypter.R
import com.streamwide.fileencrypter.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}