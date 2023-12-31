package com.streamwide.fileencrypter.presentation.commons

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import com.streamwide.fileencrypter.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension function to extract the display name of a file associated with a content Uri.
 *
 * @param context The context used to access the content resolver.
 * @return The display name of the file.
 */
fun Uri.fileName(context: Context): String {
    val uri = this
    var fileName = ""
    if (uri.scheme == "content") {
        val cursor = context.contentResolver
            .query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val cur = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileName =
                    cursor.getString(cur)
            }
        } catch (e:Exception){e.printStackTrace() }finally {
            cursor?.close()
        }
    }
    return fileName
}

/**
 * Custom exception class for representing cases where a file exceeds the maximum allowed size.
 *
 * @param message A message providing details about the exception.
 */
class MaxFileSizeException(message: String) : Exception(message)

/**
 * Extension function to convert the content of a file associated with a content Uri to a byte array.
 *
 * @param context The context used to access the content resolver.
 * @return A byte array representing the content of the file, or null if an error occurs.
 */
fun Uri.toFileByteArray(context: Context): ByteArray? {


    val fileSize = getFileSize(context)
    if (fileSize == null || fileSize > 524288000) {
        throw MaxFileSizeException(context.getString(R.string.max_size))
    }
    val inputStream =
        context.contentResolver.openInputStream(this)
    val bos = ByteArrayOutputStream()
    val b = ByteArray(fileSize.toInt())
    var bytesRead = 0
    while (inputStream?.read(b)?.also { bytesRead = it } != -1) {
        bos.write(b, 0, bytesRead)
    }
    inputStream.close()
    return bos.toByteArray()

}

/**
 * Extension function to obtain the size of a file associated with a content Uri.
 *
 * @param context The context used to access the content resolver.
 * @return The size of the file in bytes, or null if the size cannot be determined.
 */
fun Uri.getFileSize(context: Context): Long? {
    val contentResolver: ContentResolver = context.contentResolver

    // Use the DocumentsContract to query the document size for Android Q (API level 29) and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val cursor = contentResolver.query(this, null, null, null, null, null)
        cursor?.use {
            val sizeIndex = it.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE)
            if (it.moveToFirst() && sizeIndex != -1) {
                return it.getLong(sizeIndex)
            }
        }
    } else {
        // For Android versions below Q, use the MediaStore
        val projection = arrayOf(MediaStore.Images.Media.SIZE)
        val cursor = contentResolver.query(this, projection, null, null, null)
        cursor?.use {
            val sizeIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            if (it.moveToFirst()) {
                return it.getLong(sizeIndex)
            }
        }
    }

    return null
}

/**
 * Extension function to convert the size of a byte array into a human-readable string representation.
 *
 * @return A string representing the size of the byte array.
 */
fun ByteArray.sizeValue(): String {
    return when {
        size < 1000 -> "$size byte"
        size < 1000 * 1000 -> "${size / 1000} KB"
        size < 1000 * 1000 * 1000 -> "${size / (1000 * 1000)} MB"
        else -> "${size / (1000 * 1000 * 1000)} GB"
    }
}

/**
 * Generates a random string.
 *
 * @return A randomly generated string.
 */
fun randomString(): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..20)
        .map { allowedChars.random() }
        .joinToString("")
}

/**
 * Extension function to format a Calendar instance as a string with a specified format.
 *
 * @param format The desired format for the date. Default is "dd-MM-yyyy HH:mm".
 */
fun Calendar.formatDate(format: String = "dd-MM-yyyy HH:mm"): String {
    val sdfServer = SimpleDateFormat(format, Locale.FRANCE)
    return try {
        sdfServer.format(time)
    } catch (e: Exception) {
        ""
    }
}

fun deleteDirectory(directory: File): Boolean {
    if (directory.exists()) {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    // Recursive call for subdirectories
                    deleteDirectory(file)
                } else {
                    // Delete file
                    file.delete()
                }
            }
        }
    }

    // Delete the empty directory or the directory with its contents removed
    return directory.delete()
}