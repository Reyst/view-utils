package com.github.reyst.utils.view

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment


fun Activity.showToast(text: String, length: Int = Toast.LENGTH_SHORT) = Toast
    .makeText(this, text, length)
    .show()

fun Activity.showToast(@StringRes stringId: Int, length: Int = Toast.LENGTH_SHORT) = Toast
    .makeText(this, stringId, length)
    .show()

fun Fragment.showToast(text: String, length: Int = Toast.LENGTH_SHORT) =
    requireActivity().showToast(text, length)

@Suppress("unused")
fun Fragment.showToast(@StringRes stringId: Int, length: Int = Toast.LENGTH_SHORT) =
    requireActivity().showToast(stringId, length)

