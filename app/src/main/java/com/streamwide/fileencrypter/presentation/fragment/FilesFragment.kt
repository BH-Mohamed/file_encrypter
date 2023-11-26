package com.streamwide.fileencrypter.presentation.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.streamwide.fileencrypter.R
import com.streamwide.fileencrypter.databinding.FragmentFilesBinding
import com.streamwide.fileencrypter.domain.model.File
import com.streamwide.fileencrypter.domain.model.Resource
import com.streamwide.fileencrypter.fileHelper.FileOpener
import com.streamwide.fileencrypter.fileHelper.FilePicker
import com.streamwide.fileencrypter.presentation.adapter.FileAdapter
import com.streamwide.fileencrypter.presentation.base.BaseFragment
import com.streamwide.fileencrypter.presentation.dialog.FilePopupAction
import com.streamwide.fileencrypter.presentation.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class FilesFragment : BaseFragment(), FilePicker.OnFilePickerListener, FileAdapter.FileListener {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: FragmentFilesBinding
    private lateinit var adapter: FileAdapter
    private val files = ArrayList<File>()

    private val filePicker = FilePicker(this, this)
    private val fileOpener = FileOpener(this)

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

        viewModel.getFilesList()

        binding.fabAddFile.setOnClickListener(this)
        binding.btnImport.setOnClickListener(this)

    }

    //listeners to observers
    override fun callObservers() {

        lifecycleScope.launch {
            viewModel.saveFileObserver.collect {


                if(files.isEmpty()){
                    binding.contentListEmpty.isVisible =  it !is Resource.Loading
                    binding.progress.isVisible =  it is Resource.Loading
                }else{
                    binding.progressSavingFile.isVisible = it is Resource.Loading || viewModel.currentProcessingFile>0
                    binding.imageFolder.visibility = if(it is Resource.Loading || viewModel.currentProcessingFile>0) View.INVISIBLE else View.VISIBLE

                }

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

                    binding.contentListEmpty.isVisible = files.isEmpty()
                    binding.contentListNotEmpty.isVisible = files.isNotEmpty()
                }

            }
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.fab_add_file,R.id.btn_import -> {
                //open file picker
                filePicker.pickFile(requireActivity())
            }
        }
    }

    override fun onFilePicked(uri: Uri) {
        viewModel.encryptFileAndSaveIt(requireContext(), uri)
    }

    override fun onFilePickerFailed() {
        Toast.makeText(requireContext(),getString(R.string.error_fetch_file),Toast.LENGTH_LONG).show()
    }

    override fun onFileClickListener(file: File) {

        //open popup actions
        FilePopupAction(requireActivity(), file, object : FilePopupAction.FilePopupListener {

            override fun openFile(file: File,popup: FilePopupAction) {

                popup.loading(true)
                // lunch  Coroutine with IO dispatcher to decrypt the file
                // return the decrypted file with a callback function
                viewModel.decryptFile(requireContext(),file){decryptedFile->

                    //dismiss popup via main thread
                    lifecycleScope.launch {
                        launch(Dispatchers.Main) {
                            popup.dismiss()
                        }
                    }
                    //open file with system and deleted when closed
                    fileOpener.openFileFromSystem(requireActivity(),decryptedFile)

                }

            }

            override fun cancelOpenFile() {
                //cancel job decryption file if exists
                viewModel.cancelDecryptingFile()

            }

            override fun deleteFile(file: File) {
                //delete encrypted file from storage and DB
                viewModel.deleteFile(file)
            }

            override fun showDetail(file: File) {
                //navigate to file detail
                findNavController().navigate(FilesFragmentDirections.actionFilesFragmentToAddFileFragment(file.id))
            }

        }).show()


    }

    override fun onStop() {
        //stop progressing
        //cancel coroutine job if exists
        viewModel.cancelDecryptingFile()
        super.onStop()
    }
}