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
import com.pyamsoft.cachify.internal.BaseCacheCaller
import com.pyamsoft.cachify.storage.CacheStorage
import com.pyamsoft.cachify.storage.MemoryCacheStorage
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

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
  return object :
      Cached<R>,
      BaseCacheCaller<R>(
          context = context,
          debugTag = debugTag,
          storage = storage(),
      ) {

    private val operation: suspend CoroutineScope.() -> R = { upstream(this) }

    override suspend fun call(): R =
        withContext(context = context) { orchestrator.fetch(operation) }
  }
}
