# Tajawal Hotels

Portfolio-ready Android sample focused on clean separation of concerns, pragmatic engineering decisions, and a classic MVP implementation that remains easy to explain in interviews.

This app presents a small hotel browsing experience: users can load a list of hotels, open a details screen with pricing and location data, and inspect the main hotel image in a dedicated viewer. The codebase intentionally preserves its original architectural style instead of being retrofitted into a newer pattern, which makes it a good example of working effectively within an existing Android codebase rather than rewriting it unnecessarily.

## Why This Project Is Worth Reviewing

This repository is a strong conversation piece for recruiters and hiring teams because it demonstrates:

- contract-driven MVP screens
- a Repository layer coordinating remote and local data access
- Dagger 2 dependency injection in a real app flow
- RxJava-based asynchronous work
- lightweight caching for faster repeat access
- presenter-focused unit testing
- thoughtful modernization opportunities without architecture churn

## App Goal

The product scope is intentionally compact, but realistic enough to show engineering fundamentals:

- fetch hotel data from a remote endpoint
- cache the latest hotel list locally
- render a browsable hotel list
- show hotel details, pricing, address, image, and map location
- open the hotel image in a full-screen viewer

That smaller feature set helps the architecture stay visible. Reviewers can quickly understand where UI logic lives, where data is fetched, how dependencies are wired, and how the project could evolve over time.

## Stack

### Core

- Kotlin 1.4.10
- Android Gradle Plugin 4.1.0
- Single Android application module
- `minSdkVersion 19`
- `targetSdkVersion 27`
- Android Support Libraries (pre-AndroidX)

### Architecture and Infrastructure

- MVP (Model-View-Presenter)
- Repository pattern
- Dagger 2
- Retrofit 2
- Gson
- OkHttp + logging interceptor
- RxJava 2 + RxAndroid 2
- PaperDB for lightweight local persistence
- Glide for image loading
- Google Maps SDK

### Testing

- JUnit 4
- Mockito
- Mockito Kotlin
- Android instrumentation test scaffolding with Espresso

## Architecture

### MVP + Repository

The application follows a classic Android MVP approach:

- `Activity` classes act as Views
- `Contract` interfaces define the responsibilities between View and Presenter
- `Presenter` classes coordinate screen behavior and UI decisions
- `HotelsRepository` abstracts data retrieval and local persistence details from the presentation layer

This structure keeps Android framework concerns concentrated in the View layer while leaving orchestration in presenters and data access inside the repository/provider stack.

### Data Flow

The list flow is simple and explicit:

1. `HotelListPresenter` requests hotel data from `HotelsRepository`.
2. The repository calls the remote API through Retrofit.
3. On success, the response is persisted locally through `HotelProvider` using PaperDB.
4. The repository emits the first non-empty result available.
5. The presenter updates the View with loading, success, and error states.

The details flow is intentionally cache-first. Once the list has been loaded, the details screen resolves the selected hotel locally by ID instead of issuing another endpoint call.

### Dependency Injection

Dagger 2 is used to wire the app through:

- `AppComponent` for application-wide dependencies
- `ActivityBuilder` for activity injection
- dedicated modules for settings, network, repository, provider, and per-screen contracts

This is a traditional setup, but it keeps construction explicit and makes presenter testing straightforward.

## Package Structure

```text
TajawalProgrammingTest/
├── app/
│   ├── src/main/java/com/renatoramos/tajawal/
│   │   ├── common/
│   │   │   ├── constants/
│   │   │   ├── di/
│   │   │   ├── extensions/
│   │   │   └── ui/
│   │   ├── data/
│   │   │   ├── model/
│   │   │   └── store/
│   │   │       ├── local/
│   │   │       └── remote/
│   │   ├── presentation/
│   │   │   ├── base/
│   │   │   └── ui/hotel/
│   │   │       ├── list/
│   │   │       ├── detail/
│   │   │       └── imageviewer/
│   │   └── MainApplication.kt
│   ├── src/test/
│   └── src/androidTest/
└── design/
```

### Package Responsibilities

- `common`: application-wide infrastructure such as DI, constants, extensions, scopes, and reusable UI helpers
- `data`: models, repository, local provider, and remote service definitions
- `presentation`: base MVP contracts plus the screen-specific presenters, activities, adapters, and modules

## Screens Included

### Hotel List

The home screen displays the available hotels in a scrollable layout and delegates all loading behavior to `HotelListPresenter`.

### Hotel Details

The details screen renders:

- hotel name
- discounted and original price
- address
- main image
- map marker using latitude and longitude

### Full-Screen Image Viewer

The image viewer offers a focused visual detail flow and rounds out the app with a small but polished interaction.

## Testing Strategy

The current testing approach focuses on presenter behavior, which matches the architecture well because presenters hold the orchestration logic.

Covered tests include:

- `HotelListPresenterTest`
- `DetailsPresenterTest`
- `ImageViewerPresenterTest`

These tests verify concerns such as:

- screen setup calls during `onStart`
- repository interaction
- success rendering
- error propagation
- navigation and image-opening triggers

This is a sensible trade-off for an MVP codebase: most business-facing UI behavior can be validated without booting Android framework components.

## Screenshots

Screenshots live in the [`design/`](design) folder.

| Hotel list | Hotel list |
| --- | --- |
| ![Hotel list screen](design/Screenshot1.png) | ![Hotel list alternate state](design/Screenshot2.png) |

| Hotel details | Hotel details |
| --- | --- |
| ![Hotel details screen](design/Screenshot3.png) | ![Hotel details alternate state](design/Screenshot4.png) |

| Image viewer |
| --- |
| ![Image viewer screen](design/Screenshot5.png) |

## Build and Run

### Requirements

- Android Studio
- Android SDK 27
- JDK compatible with Gradle and AGP 4.1.0

### Local Setup

```bash
cd TajawalProgrammingTest
./gradlew assembleDebug
```

Then run the `app` configuration from Android Studio on an emulator or physical device.

## Trade-offs

This project is intentionally presented as a well-structured legacy Android sample, not as a rewritten modern template.

### Strengths

- Clear separation between view logic, orchestration, and data access
- Easy-to-follow presenter contracts
- Repository abstraction hides data origin from the UI
- Explicit dependency graph
- Lightweight persistence improves repeat navigation flows
- Architecture is approachable during code reviews and interviews

### Trade-offs

- Uses Android Support Libraries instead of AndroidX
- Uses Kotlin Android synthetics instead of View Binding
- Keeps everything in a single module, so boundaries are organizational rather than enforced at build level
- Relies on RxJava 2 instead of coroutines and Flow
- Cache strategy is intentionally simple and does not attempt full offline synchronization
- MVP adds additional interfaces and boilerplate compared with newer UI patterns

## Modernization Notes

The goal of this repository is not to migrate architecture. If this codebase were evolved today, the most responsible path would be incremental modernization while preserving MVP + Repository:

1. Migrate Support Libraries to AndroidX.
2. Upgrade Gradle, Kotlin, and library versions.
3. Replace Kotlin synthetics with View Binding.
4. Refine UI state handling for loading, success, and error rendering.
5. Strengthen repository semantics around cache fallback and failure cases.
6. Add repository-level tests and a few higher-value UI/instrumentation tests.
7. Consider modularization only if the scope grows enough to justify it.

The important point is architectural restraint: this project is intentionally kept in MVP so it can demonstrate maintenance and improvement of an existing pattern, not an unnecessary rewrite.

## Recruiter Notes

This repository is best read as evidence of:

- comfort working inside established Android architectures
- ability to keep responsibilities separated and testable
- experience with dependency injection, caching, networking, and reactive flows
- judgment to modernize a codebase progressively instead of defaulting to large migrations

## License

Licensed under the MIT License. See [LICENSE](LICENSE).
