# Development Environment

The app is written entirely in Kotlin. 
Used [Couroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) for asynchronous and non-blocking code.

# Architecture

The architecture is built around
[Android Architecture Components](https://developer.android.com/topic/libraries/architecture/). 

Logic kept away from Activity and moved it to
[ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel).

Data is observed using
[LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
and used [Kotlin View Binding Extension](https://kotlinlang.org/docs/tutorials/android-plugin.html)
to bind UI components in layouts to the app's data sources.

For loading and diaplaying small chunks of result data at a time used
[Paging](https://developer.android.com/topic/libraries/architecture/paging)

Used [Dagger2](https://github.com/google/dagger) for dependency injection
and heavily relied on [dagger-android](https://google.github.io/dagger/android.html) to abstract away
boiler-plate code.

# Third-party libraries

[Retrofit](https://square.github.io/retrofit) for communication with server side.

[OkHttp](https://square.github.io/okhttp) for HTTP application network.

[Gson](https://github.com/google/gson) for deserialization to convert JSON into Objects. 

[Glide](https://github.com/bumptech/glide) for image loading.

[Timber](https://github.com/JakeWharton/timber) for logging.

[Mockito](https://github.com/mockito/mockito) for unit testing.
