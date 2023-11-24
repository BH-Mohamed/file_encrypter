package com.streamwide.fileencrypter.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.streamwide.fileencrypter.R
import com.streamwide.fileencrypter.databinding.FragmentDetailFileBinding
import com.streamwide.fileencrypter.domain.model.File
import com.streamwide.fileencrypter.domain.model.Resource
import com.streamwide.fileencrypter.fileHelper.FileOpener
import com.streamwide.fileencrypter.presentation.base.BaseFragment
import com.streamwide.fileencrypter.presentation.commons.formatDate
import com.streamwide.fileencrypter.presentation.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFileFragment : BaseFragment() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: FragmentDetailFileBinding
    private lateinit var file: File
    private val fileOpener = FileOpener(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return FragmentDetailFileBinding.inflate(inflater).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callObservers()

        viewModel.getFileById(DetailFileFragmentArgs.fromBundle(requireArguments()).fileId)

        binding.btnBack.setOnClickListener(this)
        binding.btnOpenFile.setOnClickListener(this)
        binding.btnDelete.setOnClickListener(this)

    }

    private fun callObservers() {
        lifecycleScope.launch {
            viewModel.fileDetailObserver.collect {
                if (it is Resource.Success) {
                    it.also {f-> file=f.data!! }
                    binding.fileName.text = getString(R.string.file_name, it.data?.name ?: "")
                    binding.fileCreatedAt.text =
                        getString(R.string.created_at, it.data?.createdAt?.formatDate())
                    binding.fileSize.text = getString(R.string.file_size, it.data?.size ?: "")
                }
            }
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.btn_back -> {
                findNavController().popBackStack()
            }
            R.id.btn_open_file -> {


                // lunch  Coroutine with IO dispatcher to decrypt the file
                // return the decrypted file with a callback function
                viewModel.decryptFile(requireContext(), file) { decryptedFile ->

                    //open file with system and deleted when closed
                    fileOpener.openFileFromSystem(requireActivity(), decryptedFile)

                }


            }
            R.id.btn_delete -> {
                viewModel.deleteFile(file)
                findNavController().popBackStack()
            }
        }
    }
}