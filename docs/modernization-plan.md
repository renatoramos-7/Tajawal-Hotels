# Modernization Plan

## Current Snapshot

The project is a well-structured legacy Android app with clear separation of concerns, but it is anchored to an old platform baseline:

- Kotlin `1.4.10`
- Android Gradle Plugin `4.1.0`
- `compileSdkVersion` / `targetSdkVersion` `27`
- Android Support Libraries instead of AndroidX
- `kotlin-android-extensions` synthetic view access
- RxJava 2, Dagger 2, MVP, XML layouts, PaperDB

That makes the best modernization strategy incremental. The code should first become build-stable and upgrade-safe before any architectural rewrite is attempted.

## Recommended Execution Order

### Phase 1: Build and Platform Baseline

Goal: make the project buildable, reproducible, and compatible with a modern Android toolchain.

Primary work:

- standardize local SDK setup and document build prerequisites
- remove `jcenter()` usage
- migrate from Support Libraries to AndroidX
- replace `kotlin-android-extensions`
- raise `compileSdkVersion` and `targetSdkVersion`
- upgrade Gradle wrapper, Android Gradle Plugin, and Kotlin in compatible steps

Main risks:

- dependency incompatibilities during AndroidX migration
- AGP and Kotlin version jumps breaking kapt or test configuration
- hidden manifest/resource regressions after SDK target changes

Most affected files:

- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/build.gradle`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/build.gradle`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/gradle/wrapper/gradle-wrapper.properties`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/gradle.properties`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/AndroidManifest.xml`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/ui/hotel/list/HotelListActivity.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/ui/hotel/detail/DetailsActivity.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/ui/hotel/imageviewer/ImageViewerActivity.kt`

### Phase 2: Safety, Nullability, and Test Reliability

Goal: reduce regression risk before larger refactors.

Primary work:

- remove unsafe `!!` access and tighten model nullability
- isolate scheduler usage so presenters and repositories are easier to test
- expand unit tests around repository and presenter edge cases
- make offline/cache behavior explicit in tests

Main risks:

- behavior changes hidden inside null-handling cleanup
- tests exposing current assumptions that the UI silently depends on
- repository contracts changing before the UI is adapted

Most affected files:

- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/data/store/HotelsRepository.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/data/store/local/HotelProvider.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/base/BasePresenter.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/ui/hotel/list/HotelListPresenter.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/ui/hotel/detail/DetailsPresenter.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/test/java/com/renatoramos/tajawal/hotel/list/HotelListPresenterTest.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/test/java/com/renatoramos/tajawal/hotel/detail/DetailsPresenterTest.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/test/java/com/renatoramos/tajawal/hotel/imageviewer/ImageViewerPresenterTest.kt`

### Phase 3: UI Modernization Without Architectural Rewrite

Goal: modernize the UI layer while preserving delivery speed.

Primary work:

- replace synthetic bindings with View Binding
- clean up Activity responsibilities
- standardize loading, empty, and error rendering
- improve adapter and item rendering contracts

Main risks:

- accidental UI regressions during view binding migration
- duplicated responsibilities between Activities and Presenters
- layout assumptions surfacing during screen-state cleanup

Most affected files:

- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/res/layout/activity_hotel_list.xml`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/res/layout/activity_detail.xml`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/res/layout/activity_image_viewer.xml`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/res/layout/hotel_viewholder.xml`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/ui/hotel/list/HotelListActivity.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/ui/hotel/detail/DetailsActivity.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/ui/hotel/imageviewer/ImageViewerActivity.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/ui/hotel/list/adapters/HotelListRecyclerAdapter.kt`

### Phase 4: Data and Dependency Modernization

Goal: modernize infrastructure without forcing a full app rewrite.

Primary work:

- evaluate replacing PaperDB with Room or another typed local source
- upgrade Retrofit/OkHttp/Glide/Dagger to supported versions
- centralize network and cache policies
- introduce clearer boundaries between data source, repository, and mapping

Main risks:

- migration cost in persistence and caching behavior
- API surface changes in old library integrations
- Dagger setup churn causing injection breakages across screens

Most affected files:

- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/common/di/AppComponent.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/common/di/module/NetworkModule.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/common/di/module/RepositoryModule.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/data/store/HotelsRepository.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/data/store/local/HotelProvider.kt`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/data/store/remote/network/NetworkService.kt`

### Phase 5: Architectural Evolution

Goal: evolve the app only after the platform and data layers are stable.

Primary work:

- decide whether MVP remains sufficient or if selective MVVM adoption is justified
- migrate one screen at a time if architecture changes are approved
- consider replacing Dagger Android with Hilt only if the previous phases are stable

Main risks:

- spending effort on architectural fashion instead of product value
- large rewrites reducing interview readability and maintainability
- mixing paradigms across screens for too long

Most affected files:

- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/presentation/`
- `/Users/renatoramos/Documents/Projects/Personal/Android/Tajawal-Hotels/TajawalProgrammingTest/app/src/main/java/com/renatoramos/tajawal/common/di/`

## First Practical Implementation Recommendation

The first implementation should be a build-stabilization slice, not an architecture refactor.

Recommended first delivery:

1. document the exact local build prerequisites
2. remove `jcenter()` from the root build
3. enable AndroidX flags in Gradle properties
4. replace `kotlin-android-extensions` with View Binding in one screen only, starting with the hotel list flow

Why this first:

- it reduces future migration risk immediately
- it unlocks safer dependency and SDK upgrades
- it creates a repeatable migration pattern for the other screens
- it avoids mixing infrastructure migration with business-logic changes

## Immediate Notes From This Review

- the current build validation is blocked locally because the Android SDK path is not configured, so build reproducibility should be treated as Phase 1 work
- repository code currently owns threading decisions, which makes later coroutine or test migration harder
- synthetic view access and Support Libraries are the two clearest modernization pressure points in the UI layer
- the project already has enough test structure to support incremental modernization if the build baseline is fixed first
