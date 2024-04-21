package com.radio.broadcast.core.common.ui

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableList<out T>(val data: List<T>) : List<T> by data

fun <T> List<T>.toImmutableList(): ImmutableList<T> = ImmutableList(this)

fun <T> immutableListOf(vararg elements: T): ImmutableList<T> = elements.asList().toImmutableList()

fun <T> emptyImmutableList(): ImmutableList<T> = emptyList<T>().toImmutableList()