package com.github.reyst.utils.view.binding

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("unused", "UnusedReceiverParameter")
inline fun <reified T : ViewBinding> Activity.viewBinding() =
    ActivityViewBindingDelegate(T::class.java)

class ActivityViewBindingDelegate<T : ViewBinding>(
    private val bindingClass: Class<T>
) : ReadOnlyProperty<Activity, T> {
    private var binding: T? = null

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Activity, property: KProperty<*>): T {
        binding?.let { return it }
        val inflateMethod = bindingClass.getMethod("inflate", LayoutInflater::class.java)
        val invokeLayout = inflateMethod.invoke(null, thisRef.layoutInflater) as T

        return invokeLayout
            .also { this.binding = it }
    }
}

@Suppress("unused")
inline fun <reified T : ViewBinding> Fragment.viewBinding(): ReadOnlyProperty<Fragment, T> {
    val bindingClass = T::class.java
    val bindMethod = bindingClass.getMethod("bind", View::class.java)

    @Suppress("UNCHECKED_CAST")
    val bindingFactory: (View) -> T = { view -> bindMethod.invoke(null, view) as T }
    return FragmentViewBindingDelegate(this, bindingFactory)
}

@Suppress("unused")
inline fun <reified T : ViewBinding> Fragment.viewBinding(
    noinline bindingFactory: (View) -> T
): ReadOnlyProperty<Fragment, T> = FragmentViewBindingDelegate(this, bindingFactory)

class FragmentViewBindingDelegate<T : ViewBinding>(
    fragment: Fragment,
    private val bindingFactory: (View) -> T,
) : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {

    private var binding: T? = null

    init {
        fragment.viewLifecycleOwnerLiveData
            .observe(fragment) { owner ->
                owner?.lifecycle?.addObserver(this)
            }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        owner.lifecycle.removeObserver(this)
        destroyBinding()
    }

    private fun destroyBinding() {
        binding = null
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        binding
            ?.takeIf { it.root == thisRef.view }
            ?.let { return it }

        destroyBinding()

        val viewLifecycle = thisRef.viewLifecycleOwner.lifecycle
        val currentState = viewLifecycle.currentState
        if (
            !currentState.isAtLeast(Lifecycle.State.INITIALIZED)
            || currentState == Lifecycle.State.DESTROYED
        ) error("Cannot access view bindings. View lifecycle is ${currentState}!")

        return bindingFactory(thisRef.requireView())
            .also { binding = it }
    }
}
