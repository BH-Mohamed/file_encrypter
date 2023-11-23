package com.streamwide.fileencrypter.presentation.commons

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun Uri.fileName( context : Context) : String{
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
        } finally {
            cursor?.close()
        }
    }
    return fileName
}

fun Uri.toFileByteArray(context: Context) : ByteArray{
    val inputStream =
        context.contentResolver.openInputStream(this)
    val bos = ByteArrayOutputStream()
    val b = ByteArray(2048*2048*10)
    var bytesRead = 0
    while (inputStream?.read(b)?.also { bytesRead = it } != -1) {
        bos.write(b, 0, bytesRead)
    }
    inputStream.close()
    return  bos.toByteArray()
}

fun ByteArray.sizeValue() : String{
    return when {
        size < 1000 -> "$size byte"
        size < 1000*1000 -> "${size/1000} KB"
        size < 1000*1000*1000 -> "${size/(1000*1000)} MB"
        else -> "${size/(1000*1000*1000)} GB"
    }
}

fun randomString(): String {
    val generator = Random()
    val randomStringBuilder = StringBuilder()
    val randomLength: Int = generator.nextInt(120)
    var tempChar: Char
    for (i in 0 until randomLength) {
        tempChar = (generator.nextInt(96) + 32).toChar()
        randomStringBuilder.append(tempChar)
    }
    return randomStringBuilder.toString()
}

fun Calendar.formatDate( format : String = "dd-MM-yyyy HH:mm:ss"): String{
    val sdfServer = SimpleDateFormat(format, Locale.FRANCE)
    return try {
          sdfServer.format(time)
    }catch (e : Exception){
        ""
    }

}