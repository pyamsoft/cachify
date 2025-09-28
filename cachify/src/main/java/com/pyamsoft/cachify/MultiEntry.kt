/*
 * Copyright 2025 pyamsoft
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:JvmMultifileClass
@file:JvmName("Cachify")

package com.pyamsoft.cachify

import androidx.annotation.CheckResult
import com.pyamsoft.cachify.CachifyDefaults.DEFAULT_TIME
import com.pyamsoft.cachify.CachifyDefaults.DEFAULT_UNIT
import com.pyamsoft.cachify.internal.BaseMultiCacheCaller
import com.pyamsoft.cachify.internal.BaseMultiCached
import com.pyamsoft.cachify.storage.CacheStorage
import com.pyamsoft.cachify.storage.MemoryCacheStorage
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.withLock

/** Create a debugTag for each multi-caller cache entry */
@CheckResult
@PublishedApi
internal fun <K : Any> K.tag(debugTag: String): String {
  return if (debugTag.isBlank()) "" else "$debugTag-$this"
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <K : Any, V : Any> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.() -> V,
): MultiCached<K, V> {
  return object :
      MultiCached<K, V>,
      BaseMultiCached<K, MultiCached.Caller<V>>(
          context = context,
      ) {

    override suspend fun key(key: K): MultiCached.Caller<V> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              MultiCached.Caller<V>,
              BaseMultiCacheCaller<V>(
                  context = context,
                  debugTag = key.tag(debugTag),
                  storage = storage(),
              ) {

            // Cache the operation here to avoid re-allocation
            private val operation: suspend CoroutineScope.() -> V = { upstream(this) }

            override suspend fun call(): V = orchestrator.fetch(operation)
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <K : Any, V : Any, T1> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1) -> V,
): MultiCached1<K, V, T1> {
  return object :
      MultiCached1<K, V, T1>,
      BaseMultiCached<K, MultiCached1.Caller<V, T1>>(
          context = context,
      ) {

    override suspend fun key(key: K): MultiCached1.Caller<V, T1> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              MultiCached1.Caller<V, T1>,
              BaseMultiCacheCaller<V>(
                  context = context,
                  debugTag = key.tag(debugTag),
                  storage = storage(),
              ) {

            override suspend fun call(p1: T1): V = orchestrator.fetch { upstream(p1) }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2) -> V,
): MultiCached2<K, V, T1, T2> {
  return object :
      MultiCached2<K, V, T1, T2>,
      BaseMultiCached<K, MultiCached2.Caller<V, T1, T2>>(
          context = context,
      ) {

    override suspend fun key(key: K): MultiCached2.Caller<V, T1, T2> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              MultiCached2.Caller<V, T1, T2>,
              BaseMultiCacheCaller<V>(
                  context = context,
                  debugTag = key.tag(debugTag),
                  storage = storage(),
              ) {

            override suspend fun call(
                p1: T1,
                p2: T2,
            ): V = orchestrator.fetch { upstream(p1, p2) }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3) -> V,
): MultiCached3<K, V, T1, T2, T3> {
  return object :
      MultiCached3<K, V, T1, T2, T3>,
      BaseMultiCached<K, MultiCached3.Caller<V, T1, T2, T3>>(
          context = context,
      ) {

    override suspend fun key(key: K): MultiCached3.Caller<V, T1, T2, T3> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              MultiCached3.Caller<V, T1, T2, T3>,
              BaseMultiCacheCaller<V>(
                  context = context,
                  debugTag = key.tag(debugTag),
                  storage = storage(),
              ) {

            override suspend fun call(
                p1: T1,
                p2: T2,
                p3: T3,
            ): V = orchestrator.fetch { upstream(p1, p2, p3) }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> V,
): MultiCached4<K, V, T1, T2, T3, T4> {
  return object :
      MultiCached4<K, V, T1, T2, T3, T4>,
      BaseMultiCached<K, MultiCached4.Caller<V, T1, T2, T3, T4>>(
          context = context,
      ) {

    override suspend fun key(key: K): MultiCached4.Caller<V, T1, T2, T3, T4> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              MultiCached4.Caller<V, T1, T2, T3, T4>,
              BaseMultiCacheCaller<V>(
                  context = context,
                  debugTag = key.tag(debugTag),
                  storage = storage(),
              ) {

            override suspend fun call(
                p1: T1,
                p2: T2,
                p3: T3,
                p4: T4,
            ): V = orchestrator.fetch { upstream(p1, p2, p3, p4) }
          }
        }
      }
    }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@JvmOverloads
public inline fun <K : Any, V : Any, T1, T2, T3, T4, T5> multiCachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<V>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> V,
): MultiCached5<K, V, T1, T2, T3, T4, T5> {
  return object :
      MultiCached5<K, V, T1, T2, T3, T4, T5>,
      BaseMultiCached<K, MultiCached5.Caller<V, T1, T2, T3, T4, T5>>(
          context = context,
      ) {

    override suspend fun key(key: K): MultiCached5.Caller<V, T1, T2, T3, T4, T5> {
      return mutex.withLock {
        caches.getOrPut(key) {
          object :
              MultiCached5.Caller<V, T1, T2, T3, T4, T5>,
              BaseMultiCacheCaller<V>(
                  context = context,
                  debugTag = key.tag(debugTag),
                  storage = storage(),
              ) {

            override suspend fun call(
                p1: T1,
                p2: T2,
                p3: T3,
                p4: T4,
                p5: T5,
            ): V = orchestrator.fetch { upstream(p1, p2, p3, p4, p5) }
          }
        }
      }
    }
  }
}
