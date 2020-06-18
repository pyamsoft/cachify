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
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any> cachify(
    debugTag: String = "",
    storage: CacheStorage<String, R> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    upstream: suspend CoroutineScope.() -> R
): Cached<R> {
    return cachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any> cachify(
    debugTag: String = "",
    storage: List<CacheStorage<String, R>>,
    upstream: suspend CoroutineScope.() -> R
): Cached<R> {
    return object : Cached<R> {

        private val conductor = CacheOrchestrator(debugTag, storage)
        private val key = randomId()

        override suspend fun clear() {
            conductor.clear()
        }

        override suspend fun call(): R {
            return conductor.cache(key, upstream)
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1> cachify(
    debugTag: String = "",
    storage: CacheStorage<String, R> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    upstream: suspend CoroutineScope.(T1) -> R
): Cached1<R, T1> {
    return cachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1> cachify(
    debugTag: String = "",
    storage: List<CacheStorage<String, R>>,
    upstream: suspend CoroutineScope.(T1) -> R
): Cached1<R, T1> {
    return object : Cached1<R, T1> {

        private val conductor = CacheOrchestrator(debugTag, storage)
        private val key = randomId()

        override suspend fun clear() {
            conductor.clear()
        }

        override suspend fun call(p1: T1): R {
            return conductor.cache(key) { upstream(p1) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2> cachify(
    debugTag: String = "",
    storage: CacheStorage<String, R> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    upstream: suspend CoroutineScope.(T1, T2) -> R
): Cached2<R, T1, T2> {
    return cachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2> cachify(
    debugTag: String = "",
    storage: List<CacheStorage<String, R>>,
    upstream: suspend CoroutineScope.(T1, T2) -> R
): Cached2<R, T1, T2> {
    return object : Cached2<R, T1, T2> {

        private val conductor = CacheOrchestrator(debugTag, storage)
        private val key = randomId()

        override suspend fun clear() {
            conductor.clear()
        }

        override suspend fun call(p1: T1, p2: T2): R {
            return conductor.cache(key) { upstream(p1, p2) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3> cachify(
    debugTag: String = "",
    storage: CacheStorage<String, R> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    upstream: suspend CoroutineScope.(T1, T2, T3) -> R
): Cached3<R, T1, T2, T3> {
    return cachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3> cachify(
    debugTag: String = "",
    storage: List<CacheStorage<String, R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3) -> R
): Cached3<R, T1, T2, T3> {
    return object : Cached3<R, T1, T2, T3> {

        private val conductor = CacheOrchestrator(debugTag, storage)
        private val key = randomId()

        override suspend fun clear() {
            conductor.clear()
        }

        override suspend fun call(p1: T1, p2: T2, p3: T3): R {
            return conductor.cache(key) { upstream(p1, p2, p3) }
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
    debugTag: String = "",
    storage: CacheStorage<String, R> = MemoryCacheStorage.create(time, unit),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> R
): Cached4<R, T1, T2, T3, T4> {
    return cachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4> cachify(
    debugTag: String = "",
    storage: List<CacheStorage<String, R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> R
): Cached4<R, T1, T2, T3, T4> {
    return object : Cached4<R, T1, T2, T3, T4> {

        private val conductor = CacheOrchestrator(debugTag, storage)
        private val key = randomId()

        override suspend fun clear() {
            conductor.clear()
        }

        override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4): R {
            return conductor.cache(key) { upstream(p1, p2, p3, p4) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4, T5> cachify(
    debugTag: String = "",
    storage: CacheStorage<String, R> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> R
): Cached5<R, T1, T2, T3, T4, T5> {
    return cachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4, T5> cachify(
    debugTag: String = "",
    storage: List<CacheStorage<String, R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> R
): Cached5<R, T1, T2, T3, T4, T5> {
    return object : Cached5<R, T1, T2, T3, T4, T5> {

        private val conductor = CacheOrchestrator(debugTag, storage)
        private val key = randomId()

        override suspend fun clear() {
            conductor.clear()
        }

        override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5): R {
            return conductor.cache(key) { upstream(p1, p2, p3, p4, p5) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4, T5, T6> cachify(
    debugTag: String = "",
    storage: CacheStorage<String, R> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6) -> R
): Cached6<R, T1, T2, T3, T4, T5, T6> {
    return cachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4, T5, T6> cachify(
    debugTag: String = "",
    storage: List<CacheStorage<String, R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6) -> R
): Cached6<R, T1, T2, T3, T4, T5, T6> {
    return object : Cached6<R, T1, T2, T3, T4, T5, T6> {

        private val conductor = CacheOrchestrator(debugTag, storage)
        private val key = randomId()

        override suspend fun clear() {
            conductor.clear()
        }

        override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6): R {
            return conductor.cache(key) { upstream(p1, p2, p3, p4, p5, p6) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4, T5, T6, T7> cachify(
    debugTag: String = "",
    storage: CacheStorage<String, R> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7) -> R
): Cached7<R, T1, T2, T3, T4, T5, T6, T7> {
    return cachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4, T5, T6, T7> cachify(
    debugTag: String = "",
    storage: List<CacheStorage<String, R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7) -> R
): Cached7<R, T1, T2, T3, T4, T5, T6, T7> {
    return object : Cached7<R, T1, T2, T3, T4, T5, T6, T7> {

        private val conductor = CacheOrchestrator(debugTag, storage)
        private val key = randomId()

        override suspend fun clear() {
            conductor.clear()
        }

        override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6, p7: T7): R {
            return conductor.cache(key) { upstream(p1, p2, p3, p4, p5, p6, p7) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4, T5, T6, T7, T8> cachify(
    debugTag: String = "",
    storage: CacheStorage<String, R> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Cached8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
    return cachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4, T5, T6, T7, T8> cachify(
    debugTag: String = "",
    storage: List<CacheStorage<String, R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Cached8<R, T1, T2, T3, T4, T5, T6, T7, T8> {
    return object : Cached8<R, T1, T2, T3, T4, T5, T6, T7, T8> {

        private val conductor = CacheOrchestrator(debugTag, storage)
        private val key = randomId()

        override suspend fun clear() {
            conductor.clear()
        }

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
            return conductor.cache(key) { upstream(p1, p2, p3, p4, p5, p6, p7, p8) }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
fun <R : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> cachify(
    debugTag: String = "",
    storage: CacheStorage<String, R> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Cached9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    return cachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
fun <R : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> cachify(
    debugTag: String = "",
    storage: List<CacheStorage<String, R>>,
    upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Cached9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    return object : Cached9<R, T1, T2, T3, T4, T5, T6, T7, T8, T9> {

        private val conductor = CacheOrchestrator(debugTag, storage)
        private val key = randomId()

        override suspend fun clear() {
            conductor.clear()
        }

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
            return conductor.cache(key) { upstream(p1, p2, p3, p4, p5, p6, p7, p8, p9) }
        }
    }
}
