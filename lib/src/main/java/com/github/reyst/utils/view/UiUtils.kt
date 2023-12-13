package com.github.reyst.utils.view

import android.app.Activity
import android.os.Build
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(context)

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return inflater.inflate(layoutId, this, attachToRoot)
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    val view = currentFocus ?: View(this)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.showKeyboard() = showKeyboard(true)

@Suppress("DEPRECATION")
fun View.showKeyboard(useWindowInsetsController: Boolean) {
    if (useWindowInsetsController) {
        val windowController = ViewCompat.getWindowInsetsController(this)
        if (windowController != null) {
            windowController.show(WindowInsetsCompat.Type.ime())
            return
        }
    }

    ContextCompat.getSystemService(
        context,
        InputMethodManager::class.java
    )?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}


@Suppress("DEPRECATION")
fun getMetrics(activity: Activity): DisplayMetrics {
    val displayMetrics = DisplayMetrics()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.display?.getRealMetrics(displayMetrics)
    } else {
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    }

    return displayMetrics
}

@ColorInt
fun Context.themeColor(@AttrRes attrRes: Int): Int = TypedValue()
    .apply { theme.resolveAttribute (attrRes, this, true) }
    .data

fun Context.sizeById(id: Int): Int {
    return id
        .takeIf { it != 0 }
        ?.let(resources::getDimensionPixelOffset)
        ?: id
}
