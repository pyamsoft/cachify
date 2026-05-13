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

package com.pyamsoft.cachify.test

import com.pyamsoft.cachify.CachifyDefaults
import com.pyamsoft.cachify.env.TestClock
import com.pyamsoft.cachify.storage.MemoryCacheStorage
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import org.junit.Test

public class MemoryStorageTest {

  @Test
  public fun memoryCacheStorage_EmptyByDefault(): Unit = runTest {
    val clock = TestClock.create()
    val storage =
        MemoryCacheStorage.createTest<Int>(
            duration = CachifyDefaults.DEFAULT_DURATION,
            clock = clock,
        )
    val result = storage.retrieve()
    assertNull(result)
  }

  @Test
  public fun memoryCacheStorage_CachingDataWorks(): Unit = runTest {
    val clock = TestClock.create()
    val storage =
        MemoryCacheStorage.createTest<Int>(
            duration = CachifyDefaults.DEFAULT_DURATION,
            clock = clock,
        )

    storage.cache(69)
    val result = storage.retrieve()
    assertNotNull(result)
  }

  @Test
  public fun memoryCacheStorage_ClearThenRetrieveReturnsNull(): Unit = runTest {
    val clock = TestClock.create()
    val storage =
        MemoryCacheStorage.createTest<Int>(
            duration = CachifyDefaults.DEFAULT_DURATION,
            clock = clock,
        )

    // Cache some data and immediately clear it
    // Since we are entirely in a mutex, this should correctly NOT race
    storage.cache(69)
    storage.clear()

    val result = storage.retrieve()
    assertNull(result)
  }
}
