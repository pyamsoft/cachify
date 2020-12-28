/*
 * Copyright 2020 Peter Kenji Yamanaka
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
 */

package com.pyamsoft.cachify

import com.pyamsoft.cachify.Cachify.DEFAULT_TIME
import com.pyamsoft.cachify.Cachify.DEFAULT_UNIT
import kotlinx.coroutines.CoroutineScope

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any> multiCachify(
    debugTag: String = "",
    storage: CacheStorage<K, V> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    crossinline upstream: suspend CoroutineScope.() -> V
): MultiCached<K, V> {
    return multiCachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any> multiCachify(
    debugTag: String = "",
    storage: List<CacheStorage<K, V>>,
    crossinline upstream: suspend CoroutineScope.() -> V
): MultiCached<K, V> {
    return object : MultiCached<K, V> {

        private val conductor = CacheOrchestrator(debugTag, storage)

        override suspend fun clear() {
            conductor.clear()
        }

        override fun key(key: K): MultiCached.Caller<K, V> {
            return object : MultiCached.Caller<K, V> {
                override suspend fun call(): V {
                    return conductor.cache(key, upstream)
                }

                override suspend fun clear() {
                    conductor.invalidate(key)
                }
            }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1> multiCachify(
    debugTag: String = "",
    storage: CacheStorage<K, V> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    crossinline upstream: suspend CoroutineScope.(T1) -> V
): MultiCached1<K, V, T1> {
    return multiCachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1> multiCachify(
    debugTag: String = "",
    storage: List<CacheStorage<K, V>>,
    crossinline upstream: suspend CoroutineScope.(T1) -> V
): MultiCached1<K, V, T1> {
    return object : MultiCached1<K, V, T1> {

        private val conductor = CacheOrchestrator(debugTag, storage)

        override suspend fun clear() {
            conductor.clear()
        }

        override fun key(key: K): MultiCached1.Caller<T1, K, V> {
            return object : MultiCached1.Caller<T1, K, V> {
                override suspend fun call(p1: T1): V {
                    return conductor.cache(key) { upstream(p1) }
                }

                override suspend fun clear() {
                    conductor.invalidate(key)
                }
            }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2> multiCachify(
    debugTag: String = "",
    storage: CacheStorage<K, V> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    crossinline upstream: suspend CoroutineScope.(T1, T2) -> V
): MultiCached2<K, V, T1, T2> {
    return multiCachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2> multiCachify(
    debugTag: String = "",
    storage: List<CacheStorage<K, V>>,
    crossinline upstream: suspend CoroutineScope.(T1, T2) -> V
): MultiCached2<K, V, T1, T2> {
    return object : MultiCached2<K, V, T1, T2> {

        private val conductor = CacheOrchestrator(debugTag, storage)

        override suspend fun clear() {
            conductor.clear()
        }

        override fun key(key: K): MultiCached2.Caller<T1, T2, K, V> {
            return object : MultiCached2.Caller<T1, T2, K, V> {

                override suspend fun call(p1: T1, p2: T2): V {
                    return conductor.cache(key) { upstream(p1, p2) }
                }

                override suspend fun clear() {
                    conductor.invalidate(key)
                }
            }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3> multiCachify(
    debugTag: String = "",
    storage: CacheStorage<K, V> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3) -> V
): MultiCached3<K, V, T1, T2, T3> {
    return multiCachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3> multiCachify(
    debugTag: String = "",
    storage: List<CacheStorage<K, V>>,
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3) -> V
): MultiCached3<K, V, T1, T2, T3> {
    return object : MultiCached3<K, V, T1, T2, T3> {

        private val conductor = CacheOrchestrator(debugTag, storage)

        override suspend fun clear() {
            conductor.clear()
        }

        override fun key(key: K): MultiCached3.Caller<T1, T2, T3, K, V> {
            return object : MultiCached3.Caller<T1, T2, T3, K, V> {
                override suspend fun call(p1: T1, p2: T2, p3: T3): V {
                    return conductor.cache(key) { upstream(p1, p2, p3) }
                }

                override suspend fun clear() {
                    conductor.invalidate(key)
                }
            }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4> multiCachify(
    debugTag: String = "",
    storage: CacheStorage<K, V> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> V
): MultiCached4<K, V, T1, T2, T3, T4> {
    return multiCachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4> multiCachify(
    debugTag: String = "",
    storage: List<CacheStorage<K, V>>,
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> V
): MultiCached4<K, V, T1, T2, T3, T4> {
    return object : MultiCached4<K, V, T1, T2, T3, T4> {

        private val conductor = CacheOrchestrator(debugTag, storage)

        override suspend fun clear() {
            conductor.clear()
        }

        override fun key(key: K): MultiCached4.Caller<T1, T2, T3, T4, K, V> {
            return object : MultiCached4.Caller<T1, T2, T3, T4, K, V> {
                override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4): V {
                    return conductor.cache(key) { upstream(p1, p2, p3, p4) }
                }

                override suspend fun clear() {
                    conductor.invalidate(key)
                }
            }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5> multiCachify(
    debugTag: String = "",
    storage: CacheStorage<K, V> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> V
): MultiCached5<K, V, T1, T2, T3, T4, T5> {
    return multiCachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5> multiCachify(
    debugTag: String = "",
    storage: List<CacheStorage<K, V>>,
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> V
): MultiCached5<K, V, T1, T2, T3, T4, T5> {
    return object : MultiCached5<K, V, T1, T2, T3, T4, T5> {

        private val conductor = CacheOrchestrator(debugTag, storage)

        override suspend fun clear() {
            conductor.clear()
        }

        override fun key(key: K): MultiCached5.Caller<T1, T2, T3, T4, T5, K, V> {
            return object : MultiCached5.Caller<T1, T2, T3, T4, T5, K, V> {
                override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5): V {
                    return conductor.cache(key) { upstream(p1, p2, p3, p4, p5) }
                }

                override suspend fun clear() {
                    conductor.invalidate(key)
                }
            }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6> multiCachify(
    debugTag: String = "",
    storage: CacheStorage<K, V> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6) -> V
): MultiCached6<K, V, T1, T2, T3, T4, T5, T6> {
    return multiCachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6> multiCachify(
    debugTag: String = "",
    storage: List<CacheStorage<K, V>>,
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6) -> V
): MultiCached6<K, V, T1, T2, T3, T4, T5, T6> {
    return object : MultiCached6<K, V, T1, T2, T3, T4, T5, T6> {

        private val conductor = CacheOrchestrator(debugTag, storage)

        override suspend fun clear() {
            conductor.clear()
        }

        override fun key(key: K): MultiCached6.Caller<T1, T2, T3, T4, T5, T6, K, V> {
            return object : MultiCached6.Caller<T1, T2, T3, T4, T5, T6, K, V> {
                override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5, p6: T6): V {
                    return conductor.cache(key) { upstream(p1, p2, p3, p4, p5, p6) }
                }

                override suspend fun clear() {
                    conductor.invalidate(key)
                }
            }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7> multiCachify(
    debugTag: String = "",
    storage: CacheStorage<K, V> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7) -> V
): MultiCached7<K, V, T1, T2, T3, T4, T5, T6, T7> {
    return multiCachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7> multiCachify(
    debugTag: String = "",
    storage: List<CacheStorage<K, V>>,
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7) -> V
): MultiCached7<K, V, T1, T2, T3, T4, T5, T6, T7> {
    return object : MultiCached7<K, V, T1, T2, T3, T4, T5, T6, T7> {

        private val conductor = CacheOrchestrator(debugTag, storage)

        override suspend fun clear() {
            conductor.clear()
        }

        override fun key(key: K): MultiCached7.Caller<T1, T2, T3, T4, T5, T6, T7, K, V> {
            return object : MultiCached7.Caller<T1, T2, T3, T4, T5, T6, T7, K, V> {
                override suspend fun call(
                    p1: T1,
                    p2: T2,
                    p3: T3,
                    p4: T4,
                    p5: T5,
                    p6: T6,
                    p7: T7
                ): V {
                    return conductor.cache(key) { upstream(p1, p2, p3, p4, p5, p6, p7) }
                }

                override suspend fun clear() {
                    conductor.invalidate(key)
                }
            }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7, T8> multiCachify(
    debugTag: String = "",
    storage: CacheStorage<K, V> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8) -> V
): MultiCached8<K, V, T1, T2, T3, T4, T5, T6, T7, T8> {
    return multiCachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7, T8> multiCachify(
    debugTag: String = "",
    storage: List<CacheStorage<K, V>>,
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8) -> V
): MultiCached8<K, V, T1, T2, T3, T4, T5, T6, T7, T8> {
    return object : MultiCached8<K, V, T1, T2, T3, T4, T5, T6, T7, T8> {

        private val conductor = CacheOrchestrator(debugTag, storage)

        override suspend fun clear() {
            conductor.clear()
        }

        override fun key(key: K): MultiCached8.Caller<T1, T2, T3, T4, T5, T6, T7, T8, K, V> {
            return object : MultiCached8.Caller<T1, T2, T3, T4, T5, T6, T7, T8, K, V> {
                override suspend fun call(
                    p1: T1,
                    p2: T2,
                    p3: T3,
                    p4: T4,
                    p5: T5,
                    p6: T6,
                    p7: T7,
                    p8: T8
                ): V {
                    return conductor.cache(key) { upstream(p1, p2, p3, p4, p5, p6, p7, p8) }
                }

                override suspend fun clear() {
                    conductor.invalidate(key)
                }
            }
        }
    }
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> multiCachify(
    debugTag: String = "",
    storage: CacheStorage<K, V> = MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT),
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> V
): MultiCached9<K, V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    return multiCachify(debugTag, listOf(storage), upstream)
}

/**
 * Wrapper which will generate a Cached object that delegates its call() to the upstream source
 */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5, T6, T7, T8, T9> multiCachify(
    debugTag: String = "",
    storage: List<CacheStorage<K, V>>,
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5, T6, T7, T8, T9) -> V
): MultiCached9<K, V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    return object : MultiCached9<K, V, T1, T2, T3, T4, T5, T6, T7, T8, T9> {

        private val conductor = CacheOrchestrator(debugTag, storage)

        override suspend fun clear() {
            conductor.clear()
        }

        override fun key(key: K): MultiCached9.Caller<T1, T2, T3, T4, T5, T6, T7, T8, T9, K, V> {
            return object : MultiCached9.Caller<T1, T2, T3, T4, T5, T6, T7, T8, T9, K, V> {
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
                ): V {
                    return conductor.cache(key) { upstream(p1, p2, p3, p4, p5, p6, p7, p8, p9) }
                }

                override suspend fun clear() {
                    conductor.invalidate(key)
                }
            }
        }
    }
}
