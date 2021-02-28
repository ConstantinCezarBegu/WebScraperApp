package com.constantin.webscraperapp.util

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.contentValuesOf


class FileUtils(
    private val appContext: Context
) {
    companion object {
        private const val WRITE_STORAGE_PERMISSION_REQUEST_CODE = 0x3
    }

    // Add a specific media item.
    private val contentResolver: ContentResolver = appContext.contentResolver

    fun saveBitmap(bitmap: Bitmap, name: String, activity: Activity): Boolean {
        val contentValues = contentValuesOf(
            MediaStore.MediaColumns.TITLE to name,
            MediaStore.MediaColumns.DISPLAY_NAME to name,
            MediaStore.MediaColumns.MIME_TYPE to "image/png",
            MediaStore.MediaColumns.DATE_ADDED to System.currentTimeMillis()
        )
        return if (checkPermissionForReadExternalStorage()) {
            val uri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val outputStream = contentResolver.openOutputStream(uri!!)!!
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            true
        } else {
            requestPermissionForReadExternalStorage(activity)
            false
        }
    }

    private fun checkPermissionForReadExternalStorage(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val result: Int =
                appContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    private fun requestPermissionForReadExternalStorage(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_STORAGE_PERMISSION_REQUEST_CODE
        )
    }
}
