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


    // Interface to communicate file-related actions
    interface FilePopupListener {
        /**
         * open encrypted file in system after decrypted it
         */
        fun openFile(file: File,popup : FilePopupAction)

        /**
         * cancel open file by (cancel coroutine) when the file take so long to decrypt and open
         * for example with size > 10 Mb
         * the user can cancel the process by pressing cancel button
         *
         */
        fun cancelOpenFile()

        /**
         * delete file from DB and from storage
         */
        fun deleteFile(file: File)

        /**
         * navigate to file detail
         */
        fun showDetail(file: File)
    }

    // Pop-up window and binding for UI elements
    private var popupWindow: PopupWindow? = null
    private lateinit var bindingPopup: PopupFileActionBinding

    /**
     * Function to show the pop-up window

     */
    fun show() {
        // Check if the pop-up window is not already shown
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

            // Set the file data in the pop-up
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
            bindingPopup.contentActions.setOnClickListener {}
            bindingPopup.contentLoading.setOnClickListener {}




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

    /**
     * Function to toggle loading state in the pop-up
     */
    fun loading(isLoading : Boolean= true) {

        // Toggle visibility of loading indicators
        bindingPopup.contentActions.isVisible = !isLoading
        bindingPopup.contentLoading.isVisible = isLoading
    }

}
