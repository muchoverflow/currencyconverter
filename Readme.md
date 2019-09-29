# Currency converter app

## Architecture

The app architecture follows clean architecture principles with the help of Android architecture components, data binding and RxJava. LiveData is used in the view layer since it is lifecycle aware and works out of the box with android data binding. RxJava is used in the data layer since it offers better flexibility through operatros such as zip which helps to make batch network requests.

In the initial state, the app makes couple of network requests to obtain exchange rates and currency names. These requests are then combined and stored in SQLite (Room) database for future usages. The data in the database will be valid for 30 minutes from the time it was stored.

Unit tests for the ViewModel and repository is included in this project.

## Third party libraries

- [Dagger](https://dagger.dev/) - Dependency injection
- [Gson](https://github.com/google/gson) - JSON data (De)serilization
- [Retrofit](https://github.com/square/retrofit) - Network
- [Room](https://developer.android.com/topic/libraries/architecture/room) - Database
- [RxAndroid](https://github.com/ReactiveX/RxAndroid) - Reactive data flow
- [Mockito](https://github.com/mockito/mockito) - Mocking framework for unit tests
