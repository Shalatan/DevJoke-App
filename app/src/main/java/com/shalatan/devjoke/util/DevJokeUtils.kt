package com.shalatan.devjoke.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.shalatan.devjoke.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun shareView(context: Context, view: View): Uri? {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val bgDrawable = view.background
    if (bgDrawable != null)
        bgDrawable.draw(canvas)
    else
        canvas.drawColor(ContextCompat.getColor(context, R.color.white))
    view.draw(canvas)

    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs() // don't forget to make the directory
        val stream =
            FileOutputStream("$cachePath/image.png")                // overwrites this image every time
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        Log.e("JHA", "Tu")
    } catch (e: IOException) {
        Toast.makeText(context, "Sorry !! Something unexpected occurred", Toast.LENGTH_SHORT)
            .show()
        e.printStackTrace()
    }

    val imagePath = File(context.cacheDir, "images")
    val newFile = File(imagePath, "image.png")
    val contentUri =
        FileProvider.getUriForFile(
            context,
            "com.shalatan.devjoke.fileprovider",
            newFile
        )

    return contentUri
}