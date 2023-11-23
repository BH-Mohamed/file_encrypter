package com.streamwide.fileencrypter.presentation.dialog

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.streamwide.fileencrypter.databinding.BottomSheetFileBinding
import com.streamwide.fileencrypter.domain.model.File

class FilePopupAction(
    private val activity: Activity,
    private val file: File,
    private val listener: FilePopupListener
) {


    interface FilePopupListener {
        fun openFile(file: File)
        fun deleteFile(file: File)
        fun showDetail(file: File)
    }

    private var popupWindow: PopupWindow? = null

    fun show() {
        if (popupWindow == null) {
            val inflater: LayoutInflater =
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val bindingPopup: BottomSheetFileBinding = BottomSheetFileBinding.inflate(inflater)

            popupWindow = PopupWindow(
                bindingPopup.root,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                true
            )

            bindingPopup.file = file

            bindingPopup.btnOpenFile.setOnClickListener {
                listener.openFile(file)
                dismiss()
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


            popupWindow?.setOnDismissListener {
                popupWindow = null
            }

            popupWindow?.elevation = 8f
            popupWindow?.showAsDropDown(bindingPopup.root)
        }
    }

    fun dismiss() {
        popupWindow?.dismiss()
    }

}
