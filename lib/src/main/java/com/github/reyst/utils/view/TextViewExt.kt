package com.github.reyst.utils.view

import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.TextAppearanceSpan
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun TextView.enableLink() {
    movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.setDrawableStart(drawable: Drawable?) {
    val drawables = compoundDrawablesRelative
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        drawable,
        drawables[1],
        drawables[2],
        drawables[3]
    )
}

fun TextView.setDrawableStart(@DrawableRes drawable: Int) {
    drawable
        .takeIf { it != 0 }
        ?.let { ContextCompat.getDrawable(context, it) }
        .also(::setDrawableStart)
}

fun TextView.setDrawableEnd(drawable: Drawable?) {
    val drawables = compoundDrawablesRelative
    setCompoundDrawablesRelativeWithIntrinsicBounds(
        drawables[0],
        drawables[1],
        drawable,
        drawables[3]
    )
}

fun TextView.setDrawableEnd(@DrawableRes drawable: Int) {
    drawable
        .takeIf { it != 0 }
        ?.let { ContextCompat.getDrawable(context, it) }
        .also(::setDrawableEnd)
}

fun TextView.prepareAndSet(
    @StringRes textId: Int,
    appearance: Int,
    marker: String = "#",
) = prepareAndSet(context.getString(textId), appearance, marker)

fun TextView.prepareAndSet(
    text: CharSequence,
    appearance: Int,
    marker: String = "#",
) {
    text
        .let { it to it.indexOf(marker) }
        .let { (str, index) -> str.replace(Regex.fromLiteral(marker), "") to index }
        .let { (str, index) ->
            SpannableStringBuilder(str)
                .apply {
                    setSpan(
                        TextAppearanceSpan(context, appearance),
                        0,
                        index,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                    )
                }
        }
        .also(::setText)
}
