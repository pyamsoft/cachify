package com.pyamsoft.cachify

import androidx.annotation.CheckResult
import java.util.UUID

@CheckResult
internal fun randomId(): String {
    return UUID.randomUUID().toString()
}
