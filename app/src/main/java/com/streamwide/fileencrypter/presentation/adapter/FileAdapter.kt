package com.streamwide.fileencrypter.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.streamwide.fileencrypter.databinding.ItemFileBinding
import com.streamwide.fileencrypter.domain.model.File
import com.streamwide.fileencrypter.presentation.file.FilesFragment


class FileAdapter(
    private val data: List<File>,
    private val listener: FileListener
) : RecyclerView.Adapter<FileAdapter.MainListItem>() {


    interface FileListener {
        fun onFileClickListener(file : File)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListItem {
       val inflater = LayoutInflater.from(parent.context)
        return MainListItem(ItemFileBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MainListItem, position: Int) {
        val data = data[position]
        holder.itemBinding.also {
            it.file = data
            it.cardFile.setOnClickListener {
                listener.onFileClickListener(data)
            }
        }
    }

    override fun getItemCount() = data.size

    inner class MainListItem(val itemBinding: ItemFileBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
