package com.example.sprout.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val photosDir: File
        get() = File(context.filesDir, "photos").also { it.mkdirs() }

    private val cameraDir: File
        get() = File(context.cacheDir, "camera").also { it.mkdirs() }

    fun coverFile(plantId: Long): File = File(photosDir, "plant_$plantId.jpg")

    fun tempCaptureFile(): File = File(cameraDir, "capture_temp.jpg")

    fun cameraUri(): Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        tempCaptureFile(),
    )

    fun copyFromUri(sourceUri: Uri, dest: File) {
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            val raw = input.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.size) ?: return
            val oriented = normalizeOrientation(bitmap, sourceUri)
            FileOutputStream(dest).use { out ->
                oriented.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            if (oriented !== bitmap) oriented.recycle()
            bitmap.recycle()
        }
    }

    private fun normalizeOrientation(bitmap: Bitmap, uri: Uri): Bitmap {
        val orientation = try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                ExifInterface(stream).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL,
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (_: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return bitmap
        }
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
