package com.streamwide.fileencrypter.presentation.file

import androidx.lifecycle.MutableLiveData
import com.streamwide.domain.repository.IFileRepository
import com.streamwide.fileencrypter.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(val repository: IFileRepository) : BaseViewModel() {

}