# Cachify

Simple in-memory cache of all the things

## Install

In your `build.gradle`

```gradle
dependencies {
  def latestVersion = "0.0.20"

  implementation "com.github.pyamsoft:cachify:$latestVersion"
}
```

## Usage

Let us assume you have, for example, an upstream network source using
[Retrofit](https://github.com/square/retrofit) that is fetching a `Single<List<People>>`

Life without Cachify:
```kotlin
interface RxService {

  @GET("/rx/service/names")
  fun names(filterBy: String, maxCount: Long, minLength: Int) : Single<List<People>>

}

class RxCaller {

  private val service = createService(RxService::class.java)

  fun listNames() {
    service.names(filterBy = "John", maxCount = 30L, minLength = 3)
      .map { transformPeople(it) }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe()
  }

}
```

New fancy Cachify lifestyle:
```kotlin
interface RxService {

  @GET("/rx/service/names")
  fun names(filterBy: String, maxCount: Long, minLength: Int) : Single<List<People>>

}

class RxCaller {

  private val service = createService(RxService::class.java)

  private val nameCache = cachify<Single<List<People>>, String, Long, Int> { filterBy, maxCount, minLength ->
    service.names(filterBy = filterBy, maxCount = maxCount, minLength = minLength)
  }

  suspend fun listNames() {
    nameCache.call("John", 30L, 3)
      .map { transformPeople(it) }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe()
  }

}
```

Subsequent calls to the cache instance will always return the same data as
long as the timeout period has not elapsed. The timeout period is configurable
per cache instance, and defaults to 30 seconds.

If you ever want to sidestep the cache and force the upstream data source to be
queried, or you just want to clear old cached data, you can call the `clear()` method
which will erase any cached data currently held.

You can use the `cachify` entry point for a simple cache over a single value, but for more
complex data, you can use the `multiCachify` entry point which differentiates your cached values
by using creating a mapping from unique keys to cached values.

```kotlin
interface DetailService {

  suspend fun detail(id: String, userName: String) : Details

}

class DetailCaller {

  private val service = createService(DetailService::class.java)

  private val detailCache = multiCachify<DetailId, Details, String, String> { id, userName ->
    service.detail(id = id, userName = userName)
  }

  suspend fun getDetailsFor(id: String): Details {
    return detailCache.key(id).call(id, "john.doe@example.com")
  }

}
```

## Development

Cachify is developed in the Open on GitHub at:
```
https://github.com/pyamsoft/Cachify
```
If you know a few things about Android programming and are wanting to help
out with development you can do so by creating issue tickets to squash bugs,
and propose feature requests for future inclusion.`

# Issues or Questions

Please post any issues with the code in the Issues section on GitHub. Pull Requests
will be accepted on GitHub only after extensive reading and as long as the request
goes in line with the design of the application. Pull Requests will only be
accepted for new features of the application, for general purpose bug fixes, creating
an issue is simply faster.

## License

Apache 2

```
Copyright 2020 Peter Kenji Yamanaka

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

