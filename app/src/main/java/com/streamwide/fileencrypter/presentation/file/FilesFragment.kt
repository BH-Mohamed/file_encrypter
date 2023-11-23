package com.streamwide.fileencrypter.presentation.file

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilesFragment : BaseFragment(), FilePicker.OnFilePickerListener, FileAdapter.FileListener {

    private val viewModel : MainActivityViewModel by viewModels()
    private lateinit var binding: FragmentFilesBinding
    private lateinit var adapter: FileAdapter
    private val files = ArrayList<File>()
    private val filePicker : FilePicker = FilePicker(this,this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentFilesBinding.inflate(inflater).also { binding=it }.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = FileAdapter(files,this)
        binding.filesList.adapter = adapter

        callObservers()
        viewModel.getFilesList()

        binding.fabAddFile.setOnClickListener(this)

    }

    private fun callObservers(){

        lifecycleScope.launch {
            viewModel.saveFileObserver.collect{

                binding.progressSavingFile.isVisible = it is Resource.Loading
                binding.imageFolder.isVisible = it !is Resource.Loading

                if(it is Resource.Error){
                    //snack error
                }
            }
        }

        lifecycleScope.launch{
            viewModel.filesObserver.collect{
                binding.progress.isVisible = it is Resource.Loading

                if(it is Resource.Success){
                    files.clear()
                    files.addAll(it.data?: listOf())
                    adapter.notifyDataSetChanged()
                }

            }
        }
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        when(v?.id){
            R.id.fab_add_file->{
                filePicker.importFile(requireActivity())
            }
        }
    }

    override fun onFilePicked(uri: Uri) {
        viewModel.encryptFileAndSaveIt(requireContext(),uri)
    }

    override fun onFilePickerFailed() {
        //msg failed file
    }

    override fun onFileClickListener(file: File) {

    }

}