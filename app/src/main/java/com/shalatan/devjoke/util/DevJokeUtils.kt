package com.shalatan.devjoke.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.shalatan.devjoke.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun View.hideKeyboard(context: Context?) {
    val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.showSnackBar(@StringRes message: Int, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, resources.getString(message), length)
        .setBackgroundTint(resources.getColor(R.color.dark_green))
        .show()
}

/**
 * save the view locally and return the uri of the image
 */
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

    return FileProvider.getUriForFile(
        context,
        "com.shalatan.devjoke.fileprovider",
        newFile
    )
}