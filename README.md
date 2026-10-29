# Books My Friend

Books my friend is an Android application (using kotlin) that allows users to browse and read books.

## Features

* **Gentle onboarding**: three calm intro pages you move through at your own pace, with a Skip button
* **No account needed**: just tell the app your name (or skip) and start reading
* **Home**: a personal greeting, a "Continue reading" card, popular books and category picks
* **Explore**: search by title or author (English and Bangla) and filter by category
* **Library**: books you are reading (with progress) and books you saved
* **Reader**: distraction-free pages, tap to hide controls, adjustable text size,
  paper / sepia / night page colours, and your place is saved automatically
* **Profile**: light / dark / system theme, optional wallpaper colours (Android 12+),
  English / বাংলা language switch, text size and sign out
* Warm Material 3 design, edge-to-edge layouts, large touch targets and screen-reader labels
* Works offline with bundled public-domain sample chapters

## Tech stack

* Kotlin, Android Views with ViewBinding and Material Components 3
* Android Gradle Plugin 9.5 (built-in Kotlin), Kotlin 2.4, Gradle 9.8, Java 17
* compileSdk 37, targetSdk 36, minSdk 24
* Dependency versions live in [`gradle/libs.versions.toml`](gradle/libs.versions.toml)

## Dependencies

* [Google Material Components](https://github.com/material-components/material-components-android): Material 3 widgets and theming.
* [AndroidX Core KTX](https://developer.android.com/kotlin/ktx), [AppCompat](https://developer.android.com/jetpack/androidx/releases/appcompat), [Activity](https://developer.android.com/jetpack/androidx/releases/activity), [Fragment](https://developer.android.com/jetpack/androidx/releases/fragment) and [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle).
* [AndroidX Core Splashscreen](https://developer.android.com/jetpack/androidx/releases/core): the animated launch screen.
* [AndroidX ConstraintLayout](https://developer.android.com/jetpack/androidx/releases/constraintlayout), [RecyclerView](https://developer.android.com/jetpack/androidx/releases/recyclerview) and [ViewPager2](https://developer.android.com/jetpack/androidx/releases/viewpager2).
* [Lottie](https://airbnb.io/lottie/): renders After Effects animations natively.

## Continuous integration

Every push runs [`.github/workflows/android.yml`](.github/workflows/android.yml): lint, unit tests,
debug and R8-minified release builds, then an emulator job that walks through the whole app with
Espresso (saving screenshots as a build artifact) and checks that the release build launches
without crashing.

## Permissions

None. The app works fully offline and asks for no Android permissions.

## Releases

[`.github/workflows/release.yml`](.github/workflows/release.yml) publishes a GitHub release automatically:

1. Bump `versionCode` and `versionName` in [`app/build.gradle.kts`](app/build.gradle.kts) and merge to `main`.
2. When **Android CI** passes on `main`, the workflow builds the release APK and App Bundle,
   tags the commit `v<versionName>` and attaches the files plus `SHA256SUMS.txt` to a new release.
3. If the tag already exists, nothing happens, so ordinary merges never create duplicate releases.

To sign releases with your own key, add these repository secrets
(**Settings → Secrets and variables → Actions**):

| Secret | Value |
| --- | --- |
| `KEYSTORE_BASE64` | your keystore file, base64-encoded (`base64 -w0 release.jks`) |
| `KEYSTORE_PASSWORD` | keystore password |
| `KEY_ALIAS` | key alias |
| `KEY_PASSWORD` | key password |

Without these secrets the workflow still publishes a **pre-release** whose APK is signed with a
temporary test key: fine for trying the app, but each test build must be uninstalled before the next.

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio (a version that supports AGP 9.5) with JDK 17 or newer.
3. Build and run the app, or from the command line: `./gradlew assembleDebug`.

## Contributing

Contributions are always welcome! If you have suggestions for improvements or new features, please open an issue or submit a pull request.
You can also help by reporting bugs, improving documentation, or suggesting new features.
If you want to contribute, please fork the repository and create a new branch for your feature or fix. After making your changes, submit a pull request with a clear description of what you have done and why it is needed.

See [contributing](./docs/contributing.md) for ways to get started.

Please adhere to this project's [Code of Conduct](./docs/CODE_OF_CONDUCT.md).

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
