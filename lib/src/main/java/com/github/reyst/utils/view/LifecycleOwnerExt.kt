@file:Suppress("unused")

package com.github.reyst.utils.view

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> LifecycleOwner.subscribeAtInit(
    flow: Flow<T>,
    collector: FlowCollector<T>,
) = subscribeAt(Lifecycle.State.INITIALIZED, flow, collector)

fun <T> LifecycleOwner.subscribeAtCreate(
    flow: Flow<T>,
    collector: FlowCollector<T>,
) = subscribeAt(Lifecycle.State.CREATED, flow, collector)

fun <T> LifecycleOwner.subscribeAtStart(
    flow: Flow<T>,
    collector: FlowCollector<T>,
) = subscribeAt(Lifecycle.State.STARTED, flow, collector)

fun <T> LifecycleOwner.subscribeAtResume(
    flow: Flow<T>,
    collector: FlowCollector<T>,
) = subscribeAt(Lifecycle.State.RESUMED, flow, collector)

fun <T> LifecycleOwner.subscribeAt(
    state: Lifecycle.State,
    flow: Flow<T>,
    collector: FlowCollector<T>,
): Job = lifecycleScope.launch {
    repeatOnLifecycle(state) { flow.collect(collector) }
}
