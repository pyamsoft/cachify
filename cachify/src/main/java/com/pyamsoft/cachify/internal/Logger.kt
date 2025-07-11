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

package com.pyamsoft.cachify.internal

import android.util.Log
import com.pyamsoft.cachify.CachifyDefaults

internal class Logger
internal constructor(
    private val debugTag: String,
) {

  /** Log a message if the logger is enabled */
  inline fun log(func: () -> String) {
    if (CachifyDefaults.LOGGING_ENABLED || debugTag.isNotBlank()) {
      val tag = "Cachify${if (debugTag.isNotBlank()) "[$debugTag]" else ""}"
      Log.d(tag, func())
    }
  }
}
