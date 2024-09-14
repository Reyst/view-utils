@file:Suppress("DEPRECATION", "UnusedReceiverParameter", "unused")

package com.github.reyst.utils.view.argumnts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import android.util.Size
import android.util.SizeF
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty1

//region --- Activity ---
class ActivityArgumentDelegate<T>(private val default: T) : ReadOnlyProperty<Activity, T> {
    override fun getValue(thisRef: Activity, property: KProperty<*>): T {
        @Suppress("UNCHECKED_CAST")
        return thisRef.intent.extras?.get(property.name) as? T ?: default
    }
}

inline fun <reified T> Activity.argument(defaultValue: T): ReadOnlyProperty<Activity, T> =
    ActivityArgumentDelegate(defaultValue)

inline fun <reified T> Activity.argument() =
    if (null is T) argument<T?>(null) else throw NullPointerException()

fun <T> Intent.setParams(vararg params: Pair<KProperty1<T, Any?>, Any?>): Intent {
    return apply {
        params.forEach { (param, value) -> setParam(param, value) }
    }
}

fun <T> Intent.setParam(param: KProperty1<T, Any?>, value: Any?): Intent = put(param.name, value)

private fun <T : Any?> Intent.put(key: String, value: T): Intent {
    return when (value) {
        null -> putExtra(key, null as String?) // Any nullable type will suffice.

        // Scalars
        is Boolean -> putExtra(key, value)
        is Byte -> putExtra(key, value)
        is Char -> putExtra(key, value)
        is Double -> putExtra(key, value)
        is Float -> putExtra(key, value)
        is Int -> putExtra(key, value)
        is Long -> putExtra(key, value)
        is Short -> putExtra(key, value)

        // References
        is Bundle -> putExtra(key, value)
        is CharSequence -> putExtra(key, value)
        is Parcelable -> putExtra(key, value)

        // Scalar arrays
        is BooleanArray -> putExtra(key, value)
        is ByteArray -> putExtra(key, value)
        is CharArray -> putExtra(key, value)
        is DoubleArray -> putExtra(key, value)
        is FloatArray -> putExtra(key, value)
        is IntArray -> putExtra(key, value)
        is LongArray -> putExtra(key, value)
        is ShortArray -> putExtra(key, value)

        // Reference arrays
        is Array<*> -> {
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
            val componentType = value!!::class.java.componentType!!

            @Suppress("UNCHECKED_CAST") // Checked by reflection.
            when {
                Parcelable::class.java.isAssignableFrom(componentType) -> putExtra(key, value as Array<Parcelable>)
                String::class.java.isAssignableFrom(componentType) -> putExtra(key, value as Array<String>)
                CharSequence::class.java.isAssignableFrom(componentType) -> putExtra(key, value as Array<CharSequence>)
                Serializable::class.java.isAssignableFrom(componentType) -> putExtra(key, value)
                else -> {
                    val valueType = componentType.canonicalName
                    throw IllegalArgumentException(
                        "Illegal value array type $valueType for key \"$key\""
                    )
                }
            }
        }

        // Last resort. Also we must check this after Array<*> as all arrays are serializable.
        is Serializable -> putExtra(key, value)

        else -> throw IllegalArgumentException("Illegal value type ${value.javaClass.canonicalName} for key \"$key\"")
    }
}

//endregion

//region --- Fragment ---
class FragmentArgumentDelegate<T>(private val default: T) : ReadWriteProperty<Fragment, T> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return thisRef.arguments?.get(property.name) as? T ?: default
    }

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        val args = thisRef.arguments ?: Bundle().also(thisRef::setArguments)
        args.put(property.name, value)
    }
}

inline fun <reified T> argument(defaultValue: T): ReadWriteProperty<Fragment, T> =
    FragmentArgumentDelegate(defaultValue)

inline fun <reified T> argument() =
    if (null is T) argument<T?>(null) else throw NullPointerException()

private fun <T> Bundle.put(key: String, value: T) {
    when (value) {
        null -> putString(key, null) // Any nullable type will suffice.

        // Scalars
        is Boolean -> putBoolean(key, value)
        is Byte -> putByte(key, value)
        is Char -> putChar(key, value)
        is Double -> putDouble(key, value)
        is Float -> putFloat(key, value)
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is Short -> putShort(key, value)

        // References
        is Bundle -> putBundle(key, value)
        is CharSequence -> putCharSequence(key, value)
        is IBinder -> putBinder(key, value)
        is Size -> putSize(key, value)
        is SizeF -> putSizeF(key, value)
        is Parcelable -> putParcelable(key, value)

        // Scalar arrays
        is BooleanArray -> putBooleanArray(key, value)
        is ByteArray -> putByteArray(key, value)
        is CharArray -> putCharArray(key, value)
        is DoubleArray -> putDoubleArray(key, value)
        is FloatArray -> putFloatArray(key, value)
        is IntArray -> putIntArray(key, value)
        is LongArray -> putLongArray(key, value)
        is ShortArray -> putShortArray(key, value)

        // Reference arrays
        is Array<*> -> {
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
            val componentType = value!!::class.java.componentType!!

            @Suppress("UNCHECKED_CAST") // Checked by reflection.
            when {
                Parcelable::class.java.isAssignableFrom(componentType) -> putParcelableArray(key, value as Array<Parcelable>)
                String::class.java.isAssignableFrom(componentType) -> putStringArray(key, value as Array<String>)
                CharSequence::class.java.isAssignableFrom(componentType) -> putCharSequenceArray(key, value as Array<CharSequence>)
                Serializable::class.java.isAssignableFrom(componentType) -> putSerializable(key, value)
                else -> {
                    val valueType = componentType.canonicalName
                    throw IllegalArgumentException(
                        "Illegal value array type $valueType for key \"$key\""
                    )
                }
            }
        }

        // Last resort. Also we must check this after Array<*> as all arrays are serializable.
        is Serializable -> putSerializable(key, value)

        else -> {
            val valueType = value.javaClass.canonicalName
            throw IllegalArgumentException("Illegal value type $valueType for key \"$key\"")
        }
    }
}
//endregion
