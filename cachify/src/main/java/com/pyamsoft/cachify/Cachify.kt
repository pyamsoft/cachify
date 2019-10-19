/*
 * Copyright 2019 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.pyamsoft.cachify

import com.pyamsoft.cachify.Cachify.DEFAULT_TIME
import com.pyamsoft.cachify.Cachify.DEFAULT_UNIT
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.TimeUnit

/**
 * Cachify internal constants
 */
object Cachify {

    /**
     * Default amount of time unit before cache expires
     */
    const val DEFAULT_TIME = 30L

    /**
     * Default time unit for cache expiration
     */
    val DEFAULT_UNIT = TimeUnit.SECONDS
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any> cachify(
    time: Long = DEFAULT_TIME,
    unit: TimeUnit = DEFAULT_UNIT,
    debug: Boolean = false,
    storage: CacheStorage<R> = MemoryCacheStorage.create(time, unit, debug),
    upstream: suspend CoroutineScope.() -> R
): Cached<R> {
    return cachify(debug, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any> cachify(
    debug: Boolean,
    storage: List<CacheStorage<R>>,
    upstream: suspend CoroutineScope.() -> R
): Cached<R> {
    return object : Cached<R>, CacheOrchestrator<R>(debug, storage) {

        override suspend fun call(): R {
            return callCache { upstream() }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1> cachify(
    time: Long = DEFAULT_TIME,
    unit: TimeUnit = DEFAULT_UNIT,
    debug: Boolean = false,
    storage: CacheStorage<R> = MemoryCacheStorage.create(time, unit, debug),
    upstream: suspend CoroutineScope.(T1) -> R
): Cached1<R, T1> {
    return cachify(debug, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1> cachify(
    debug: Boolean,
    storage: List<CacheStorage<R>>,
    upstream: suspend CoroutineScope.(T1) -> R
): Cached1<R, T1> {
    return object : Cached1<R, T1>, CacheOrchestrator<R>(debug, storage) {

        override suspend fun call(p1: T1): R {
            return callCache { upstream(p1) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2> cachify(
    time: Long = DEFAULT_TIME,
    unit: TimeUnit = DEFAULT_UNIT,
    debug: Boolean = false,
    storage: CacheStorage<R> = MemoryCacheStorage.create(time, unit, debug),
    upstream: suspend CoroutineScope.(T1, T2) -> R
): Cached2<R, T1, T2> {
    return cachify(debug, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2> cachify(
    debug: Boolean,
    storage: List<CacheStorage<R>>,
    upstream: suspend CoroutineScope.(T1, T2) -> R
): Cached2<R, T1, T2> {
    return object : Cached2<R, T1, T2>, CacheOrchestrator<R>(debug, storage) {

        override suspend fun call(p1: T1, p2: T2): R {
            return callCache { upstream(p1, p2) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3> cachify(
    time: Long = DEFAULT_TIME,
    unit: TimeUnit = DEFAULT_UNIT,
    debug: Boolean = false,
    storage: CacheStorage<R> = MemoryCacheStorage.create(time, unit, debug),
    upstream: suspend CoroutineScope.(T1, T2, T3) -> R
): Cached3<R, T1, T2, T3> {
    return cachify(debug, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3> cachify(
    debug: Boolean,
    storage: List<CacheStorage<R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3) -> R
): Cached3<R, T1, T2, T3> {
    return object : Cached3<R, T1, T2, T3>, CacheOrchestrator<R>(debug, storage) {

        override suspend fun call(p1: T1, p2: T2, p3: T3): R {
            return callCache { upstream(p1, p2, p3) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4> cachify(
    time: Long = DEFAULT_TIME,
    unit: TimeUnit = DEFAULT_UNIT,
    debug: Boolean = false,
    storage: CacheStorage<R> = MemoryCacheStorage.create(time, unit, debug),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> R
): Cached4<R, T1, T2, T3, T4> {
    return cachify(debug, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4> cachify(
    debug: Boolean,
    storage: List<CacheStorage<R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> R
): Cached4<R, T1, T2, T3, T4> {
    return object : Cached4<R, T1, T2, T3, T4>, CacheOrchestrator<R>(debug, storage) {

        override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4): R {
            return callCache { upstream(p1, p2, p3, p4) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4, T5> cachify(
    time: Long = DEFAULT_TIME,
    unit: TimeUnit = DEFAULT_UNIT,
    debug: Boolean = false,
    storage: CacheStorage<R> = MemoryCacheStorage.create(time, unit, debug),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> R
): Cached5<R, T1, T2, T3, T4, T5> {
    return cachify(debug, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4, T5> cachify(
    debug: Boolean,
    storage: List<CacheStorage<R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> R
): Cached5<R, T1, T2, T3, T4, T5> {
    return object : Cached5<R, T1, T2, T3, T4, T5>, CacheOrchestrator<R>(debug, storage) {

        override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5): R {
            return callCache { upstream(p1, p2, p3, p4, p5) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4, T5, T6> cachify(
    time: Long = DEFAULT_TIME,
    unit: TimeUnit = DEFAULT_UNIT,
    debug: Boolean = false,
    storage: CacheStorage<R> = MemoryCacheStorage.create(time, unit, debug),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6) -> R
): Cached6<R, T1, T2, T3, T4, T5, T6> {
    return cachify(debug, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4, T5, T6> cachify(
    debug: Boolean,
    storage: List<CacheStorage<R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6) -> R
): Cached6<R, T1, T2, T3, T4, T5, T6> {
    return object : Cached6<R, T1, T2, T3, T4, T5, T6>, CacheOrchestrator<R>(debug, storage) {

        override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6): R {
            return callCache { upstream(p1, p2, p3, p4, p5, p6) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4, T5, T6, T7> cachify(
    time: Long = DEFAULT_TIME,
    unit: TimeUnit = DEFAULT_UNIT,
    debug: Boolean = false,
    storage: CacheStorage<R> = MemoryCacheStorage.create(time, unit, debug),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7) -> R
): Cached7<R, T1, T2, T3, T4, T5, T6, T7> {
    return cachify(debug, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4, T5, T6, T7> cachify(
    debug: Boolean,
    storage: List<CacheStorage<R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7) -> R
): Cached7<R, T1, T2, T3, T4, T5, T6, T7> {
    return object : Cached7<R, T1, T2, T3, T4, T5, T6, T7>, CacheOrchestrator<R>(debug, storage) {

        override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7): R {
            return callCache { upstream(p1, p2, p3, p4, p5, p6, p7) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4, T5, T6, T7, T8> cachify(
    time: Long = DEFAULT_TIME,
    unit: TimeUnit = DEFAULT_UNIT,
    debug: Boolean = false,
    storage: CacheStorage<R> = MemoryCacheStorage.create(time, unit, debug),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Cached8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
    return cachify(debug, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4, T5, T6, T7, T8> cachify(
    debug: Boolean,
    storage: List<CacheStorage<R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Cached8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
    return object : Cached8<R, T1, T2, T3, T4, T5, T6, T7, T8>,
        CacheOrchestrator<R>(debug, storage) {

        override suspend fun call(
            p1: T1,
            p2: T2,
            p3: T3,
            p4: T4,
            p5: T5,
            p6: T6,
            p7: T7,
            p8: T8
        ): R {
            return callCache { upstream(p1, p2, p3, p4, p5, p6, p7, p8) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> cachify(
    time: Long = DEFAULT_TIME,
    unit: TimeUnit = DEFAULT_UNIT,
    debug: Boolean = false,
    storage: CacheStorage<R> = MemoryCacheStorage.create(time, unit, debug),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Cached9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    return cachify(debug, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> cachify(
    debug: Boolean,
    storage: List<CacheStorage<R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Cached9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    return object : Cached9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9>,
        CacheOrchestrator<R>(debug, storage) {

        override suspend fun call(
            p1: T1,
            p2: T2,
            p3: T3,
            p4: T4,
            p5: T5,
            p6: T6,
            p7: T7,
            p8: T8,
            p9: T9
        ): R {
            return callCache { upstream(p1, p2, p3, p4, p5, p6, p7, p8, p9) }
        }
    }
}
