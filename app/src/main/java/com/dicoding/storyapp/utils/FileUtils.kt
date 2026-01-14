package com.dicoding.storyapp.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

fun createImageTempFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("STORY_${timeStamp}_", ".jpg", storageDir)
}

fun createImageUri(context: Context, file: File): Uri {
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createImageTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var len: Int
    while (inputStream.read(buffer).also { len = it } > 0) {
        outputStream.write(buffer, 0, len)
    }
    outputStream.close()
    inputStream.close()
    return myFile
}

/**
 * Reduce image size to <= 1MB by compressing JPEG.
 * Dicoding Story API upload limit is 1 MB.
 */
fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1_000_000 && compressQuality > 5)

    // Write final compressed bitmap
    val out = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, out)
    out.flush()
    out.close()

    return file
}
