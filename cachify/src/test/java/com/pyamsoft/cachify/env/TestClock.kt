package com.pyamsoft.cachify.env

import androidx.annotation.CheckResult
import androidx.annotation.VisibleForTesting
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@VisibleForTesting
internal class TestClock
private constructor(
    instant: Instant,
    zoneId: ZoneId,
) : Clock() {

  private var clock = fixed(instant, zoneId)

  /** Use to mutate the underlying clock */
  fun setTime(instant: Instant, zoneId: ZoneId = ZoneId.systemDefault()) {
    clock = fixed(instant, zoneId)
  }

  override fun instant(): Instant {
    return clock.instant()
  }

  override fun withZone(zoneId: ZoneId): Clock {
    return TestClock(instant(), zoneId)
  }

  override fun getZone(): ZoneId {
    return clock.zone
  }

  companion object {
    @JvmStatic
    @CheckResult
    fun create(): TestClock {
      return TestClock(
          instant = Instant.now(),
          zoneId = ZoneId.systemDefault(),
      )
    }
  }
}
