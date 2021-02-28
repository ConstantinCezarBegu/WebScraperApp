package com.constantin.webscraperapp.util

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatchers {
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
    val io: CoroutineDispatcher
}
