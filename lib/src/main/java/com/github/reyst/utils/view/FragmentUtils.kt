package com.github.reyst.utils.view

import android.widget.EditText
import androidx.fragment.app.Fragment

fun Fragment.onBackPressed() = requireActivity().onBackPressedDispatcher.onBackPressed()

fun Fragment.hideKeyboard() = activity?.hideKeyboard()

fun Fragment.showKeyboardFor(editText: EditText) {
    editText.requestFocus()
    view?.post { editText.showKeyboard() }
}
