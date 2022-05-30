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

@file:JvmMultifileClass
@file:JvmName("Cachify")

package com.pyamsoft.cachify

import androidx.annotation.CheckResult
import com.pyamsoft.cachify.CachifyDefaults.DEFAULT_TIME
import com.pyamsoft.cachify.CachifyDefaults.DEFAULT_UNIT
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

/** Base class for a Cached*.Caller object */
@PublishedApi
internal abstract class BaseCaller<V : Any>
protected constructor(
    private val context: CoroutineContext,
    debugTag: String,
    storage: List<CacheStorage<V>>,
) : Cache {

  @PublishedApi
  internal val conductor: CacheOperator<V> =
      CacheOperator.create(
          context,
          debugTag,
          storage,
      )

  final override suspend fun clear() =
      withContext(context = NonCancellable) {
        // Maybe we can simplify this with a withContext(context = NonCancellable +
        // context)
        // but I don't know enough about Coroutines right now to figure out if that works
        // or if plussing the contexts will remove NonCancel, so here we go instead.
        withContext(context = context) {
          // Coroutine scope here to make sure if anything throws an error we catch it in
          // the scope
          conductor.clear()
        }
      }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <R : Any> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.() -> R
): Cached<R> {
  return object : BaseCaller<R>(context, debugTag, storage()), Cached<R> {

    private val operation: suspend CoroutineScope.() -> R = { upstream(this) }

    override suspend fun call(): R = withContext(context = context) { conductor.cache(operation) }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <R : Any, T1> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1) -> R
): Cached1<R, T1> {
  return object : BaseCaller<R>(context, debugTag, storage()), Cached1<R, T1> {

    override suspend fun call(p1: T1): R = conductor.cache { upstream(p1) }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <R : Any, T1, T2> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2) -> R
): Cached2<R, T1, T2> {
  return object : BaseCaller<R>(context, debugTag, storage()), Cached2<R, T1, T2> {

    override suspend fun call(p1: T1, p2: T2): R = conductor.cache { upstream(p1, p2) }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <R : Any, T1, T2, T3> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3) -> R
): Cached3<R, T1, T2, T3> {
  return object : BaseCaller<R>(context, debugTag, storage()), Cached3<R, T1, T2, T3> {

    override suspend fun call(p1: T1, p2: T2, p3: T3): R = conductor.cache { upstream(p1, p2, p3) }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <R : Any, T1, T2, T3, T4> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4) -> R
): Cached4<R, T1, T2, T3, T4> {
  return object : BaseCaller<R>(context, debugTag, storage()), Cached4<R, T1, T2, T3, T4> {

    override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4): R =
        conductor.cache { upstream(p1, p2, p3, p4) }
  }
}

/** Wrapper which will generate a Cached object that delegates its call() to the upstream source */
@CheckResult
@JvmOverloads
public inline fun <R : Any, T1, T2, T3, T4, T5> cachify(
    context: CoroutineContext = CachifyDefaults.DEFAULT_COROUTINE_CONTEXT,
    debugTag: String = "",
    crossinline storage: () -> List<CacheStorage<R>> = {
      listOf(MemoryCacheStorage.create(DEFAULT_TIME, DEFAULT_UNIT))
    },
    crossinline upstream: suspend CoroutineScope.(T1, T2, T3, T4, T5) -> R
): Cached5<R, T1, T2, T3, T4, T5> {
  return object : BaseCaller<R>(context, debugTag, storage()), Cached5<R, T1, T2, T3, T4, T5> {

    override suspend fun call(p1: T1, p2: T2, p3: T3, p4: T4, p5: T5): R =
        conductor.cache { upstream(p1, p2, p3, p4, p5) }
  }
}
