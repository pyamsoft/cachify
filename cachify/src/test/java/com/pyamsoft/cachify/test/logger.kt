/*
 * Copyright 2024 pyamsoft
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

import com.pyamsoft.cachify.internal.Logger
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlinx.coroutines.test.runTest

public class LoggerTest {

  @Test
  public fun logger_outputWithDebugTag(): Unit = runTest {
    val logger = Logger("ANYTHING")
    assertNotNull(logger)

    try {
      logger.log { "Log.d is not mocked so this will throw" }
    } catch (e: RuntimeException) {
      assertNotNull(e.message)
      assert(e.message!!.startsWith("Method d in android.util.Log not mocked"))
    }
  }

  @Test
  public fun logger_silentNoTag(): Unit = runTest {
    val logger = Logger("")
    assertNotNull(logger)

    logger.log { "Log.d is not mocked, but this should not run!" }
  }
}
