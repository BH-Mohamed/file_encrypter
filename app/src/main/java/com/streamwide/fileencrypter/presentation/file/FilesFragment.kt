package com.streamwide.fileencrypter.presentation.file

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.streamwide.fileencrypter.R
import com.streamwide.fileencrypter.databinding.FragmentFilesBinding
import com.streamwide.fileencrypter.domain.model.File
import com.streamwide.fileencrypter.domain.model.Resource
import com.streamwide.fileencrypter.filePicker.FilePicker
import com.streamwide.fileencrypter.presentation.adapter.FileAdapter
import com.streamwide.fileencrypter.presentation.base.BaseFragment
import com.streamwide.fileencrypter.presentation.dialog.FilePopupAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class FilesFragment : BaseFragment(), FilePicker.OnFilePickerListener, FileAdapter.FileListener {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: FragmentFilesBinding
    private lateinit var adapter: FileAdapter
    private val files = ArrayList<File>()
    private val filePicker: FilePicker = FilePicker(this, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentFilesBinding.inflate(inflater).also { binding = it }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FileAdapter(files, this)
        binding.filesList.adapter = adapter

        callObservers()
        viewModel.getFilesList()

        binding.fabAddFile.setOnClickListener(this)

    }

    private fun callObservers() {

        lifecycleScope.launch {
            viewModel.saveFileObserver.collect {

                binding.progressSavingFile.isVisible = it is Resource.Loading
                binding.imageFolder.visibility = if(it is Resource.Loading) View.INVISIBLE else View.VISIBLE

                if (it is Resource.Error) {
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.filesObserver.collect {
                binding.progress.isVisible = it is Resource.Loading

                if (it is Resource.Success) {
                    files.clear()
                    files.addAll(it.data ?: listOf())
                    adapter.notifyDataSetChanged()
                }

            }
        }
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.fab_add_file -> {
                filePicker.importFile(requireActivity())
            }
        }
    }

    override fun onFilePicked(uri: Uri) {
        viewModel.encryptFileAndSaveIt(requireContext(), uri)
    }

    override fun onFilePickerFailed() {
        //msg failed file
    }

    override fun onFileClickListener(file: File) {

        //open popup actions
        FilePopupAction(requireActivity(), file, object : FilePopupAction.FilePopupListener {

            override fun openFile(file: File) {

                //decrypt file
                val decryptedByteArray =
                    viewModel.encryptor.decryptEncryptedFile(java.io.File(file.path))

                //save decrypted file into temp dir
                val decryptedFile = viewModel.encryptor.saveFile(
                    decryptedByteArray,
                    (requireActivity().getExternalFilesDir("temp")?.absolutePath?:"")+"/${file.name}"
                )

                //open temp file on system
                //note : all temp file will be deleted in onDestroy activity
                val intent = Intent(Intent.ACTION_VIEW)
                val fileProviderPath = FileProvider.getUriForFile(
                    requireContext(),
                    requireActivity().packageName + ".fileprovider",
                    decryptedFile
                )
                intent.setDataAndType(fileProviderPath, MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension.lowercase(Locale.ROOT)))
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireActivity().startActivity(intent)



            }

            override fun deleteFile(file: File) {
                viewModel.deleteFile(file)
            }

            override fun showDetail(file: File) {

            }

        }).show()


    }


}