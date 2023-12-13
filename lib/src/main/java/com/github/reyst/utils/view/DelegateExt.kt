package com.github.reyst.utils.view

fun <T> uiLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)