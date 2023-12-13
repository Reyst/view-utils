package com.github.reyst.utils.view

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart

fun EditText.obtainTextChangesFlow(): Flow<CharSequence> = callbackFlow {
    val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            trySend(s ?: "")
        }
    }
    addTextChangedListener(listener)
    awaitClose { removeTextChangedListener(listener) }
}.onStart { emit(text ?: "") }

@FlowPreview
fun EditText.obtainDebounceTextChangesFlow(debounceDelay: Long = 300L) = obtainTextChangesFlow()
    .debounce(debounceDelay)

fun EditText.updateIfEmpty(text: String) {
    takeIf { it.text.isNullOrBlank() }
        ?.also {
            it.setText(text)
            it.setSelection(text.length)
        }
}
