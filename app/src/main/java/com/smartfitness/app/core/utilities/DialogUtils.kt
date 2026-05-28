package com.smartfitness.app.core.utilities

import android.content.Context
import android.widget.Toast

object DialogUtils {
    private var toast: Toast? = null


    fun showToast(context: Context, message: String) {
        toast?.cancel() // prevent multiple stacking

        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast?.show()
    }
}