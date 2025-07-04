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

package com.pyamsoft.cachify.storage

import androidx.annotation.CheckResult
import com.pyamsoft.cachify.Cache

/** Interface contract which Cachify uses to save data into a representation of cache */
public interface CacheStorage<T : Any> : Cache {

  /** Get any data from cache if it exists - otherwise null */
  @CheckResult public suspend fun retrieve(): T?

  /**
   * Sets new data as the active data in cache
   *
   * The new data is valid for as long as the implementation considers it valid. It may be infinite.
   */
  public suspend fun cache(data: T)
}
