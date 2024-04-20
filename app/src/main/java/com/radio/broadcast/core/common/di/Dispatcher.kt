package com.radio.broadcast.core.common.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: RadioDispatcher)

enum class RadioDispatcher {
    Default,
    IO,
}
