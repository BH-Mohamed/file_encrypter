package com.streamwide.fileencrypter.presentation.dialog

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.streamwide.fileencrypter.databinding.PopupFileActionBinding
import com.streamwide.fileencrypter.domain.model.File

class FilePopupAction(
    private val activity: Activity,
    private val file: File,
    private val listener: FilePopupListener
) {


    interface FilePopupListener {
        fun openFile(file: File,popup : FilePopupAction)
        fun cancelOpenFile()
        fun deleteFile(file: File)
        fun showDetail(file: File)
    }

    private var popupWindow: PopupWindow? = null
    private lateinit var bindingPopup: PopupFileActionBinding

    fun show() {
        if (popupWindow == null) {
            val inflater: LayoutInflater =
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            bindingPopup = PopupFileActionBinding.inflate(inflater)
            popupWindow = PopupWindow(
                bindingPopup.root,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                true
            )

            bindingPopup.file = file

            bindingPopup.btnOpenFile.setOnClickListener {
                listener.openFile(file,this@FilePopupAction)
            }

            bindingPopup.btnDetail.setOnClickListener {
                listener.showDetail(file)
                dismiss()

            }

            bindingPopup.btnDelete.setOnClickListener {
                listener.deleteFile(file)
                dismiss()

            }

            bindingPopup.content.setOnClickListener {
                popupWindow?.dismiss()
            }

            bindingPopup.btnCancel.setOnClickListener {
                popupWindow?.dismiss()
            }


            popupWindow?.setOnDismissListener {
                listener.cancelOpenFile()
                popupWindow = null
            }

            popupWindow?.elevation = 8f
            popupWindow?.showAsDropDown(bindingPopup.root)
        }
    }

     fun dismiss() {
        popupWindow?.dismiss()
    }

    fun loading(isLoading : Boolean= true) {

        bindingPopup.contentActions.isVisible = !isLoading
        bindingPopup.contentLoading.isVisible = isLoading
    }

}
