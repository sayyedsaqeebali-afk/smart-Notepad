# Smart Notepad 📝

[![Android Build APK](https://github.com/your-username/smart-notepad/actions/workflows/android-build.yml/badge.svg)](https://github.com/your-username/smart-notepad/actions/workflows/android-build.yml)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A modern, fast, and production-ready Android Notepad application built with **Kotlin**, **Jetpack Compose**, **Material Design 3**, and **Room Database**.

Smart Notepad is 100% offline and private. It features real-time auto-saving, instant search, note pinning, favorites, color tagging, live word and character counters, and adaptive theming (Light, Dark, System Default). The project is engineered to build both locally and automatically via **GitHub Actions** CI/CD without requiring Android Studio on your development machine.

---

## ✨ Features

### 📋 Notes Management
- **Create & Edit Notes**: Clean, distraction-free typography with adaptive title and multi-line content fields.
- **Real-Time Auto-Save**: Automatically commits changes to the database with intelligent debounce—no lost ideas.
- **Instant Search**: Real-time filtering across titles and note content with keyword highlights.
- **Pin to Top**: Keep critical thoughts and checklists pinned at the top of your list.
- **Favorites**: Star notes and filter them with one tap using filter chips.
- **Custom Color Accents**: Assign color tags to categorize and personalize note cards.
- **Word & Character Counters**: Live word and character analytics dynamically calculated as you type.
- **Advanced Sorting**:
  - Recently edited (default)
  - Oldest edited
  - Newest created
  - Title alphabetical (A–Z and Z–A)
- **Delete & Undo**: Safe deletion with confirmation dialog and undo snackbar support.

### 🔒 Privacy & Offline Storage
- **100% Offline**: No network tracking, no account login, and no external backends.
- **Room Database**: All notes persist across device reboots and updates using SQLite through AndroidX Room and Kotlin Symbol Processing (KSP).

### 🎨 Modern Material 3 UI
- **Material Design 3**: Dynamic surfaces, cards, and tonal elevations.
- **Theme Modes**: Full support for **System Default**, **Light Mode**, and **Dark Mode** toggleable from Settings.
- **Responsive Layout**: Adapts gracefully to phones, foldables, and tablets.
- **Smooth Micro-Interactions**: Fluid entry/exit transitions and state animations.

---

## 🏗️ Architecture & Tech Stack

The app follows the official **Android Clean Architecture** and **MVVM (Model-View-ViewModel)** guidelines:

```
app/src/main/java/com/example/
├── data/
│   ├── local/
│   │   ├── NoteDao.kt           # Room Data Access Object with reactive Flow queries
│   │   └── NoteDatabase.kt      # Room Database singleton
│   ├── model/
│   │   └── Note.kt              # Room Entity with word/char calculation logic
│   └── repository/
│       └── NoteRepository.kt    # Repository abstraction layer
├── ui/
│   ├── components/
│   │   ├── EmptyNotesView.kt    # Empty states for list, search, and filters
│   │   └── NoteCard.kt          # Note card with quick actions and stats
│   ├── editor/
│   │   └── EditorViewModel.kt   # Editor state, debounced auto-save & counters
│   ├── navigation/
│   ├── notes/
│   │   ├── NotesUiState.kt      # Immutable state model for notes view
│   │   └── NotesViewModel.kt    # Search, filter, sort & notes flow
│   ├── screens/
│   │   ├── EditorScreen.kt      # Notepad editor screen
│   │   ├── HomeScreen.kt        # Main list with search and filter chips
│   │   └── SettingsScreen.kt    # Appearance & About screen
│   ├── theme/
│   │   ├── Color.kt             # Material 3 light & dark color palette
│   │   ├── Theme.kt             # Theme wrapper with ThemeMode support
│   │   └── Type.kt              # Typography styles
│   └── SmartNotepadApp.kt       # Navigation host
└── util/
    └── DateUtils.kt             # Human-readable date & time formatter
```

- **Language**: Kotlin 2.2+
- **UI Framework**: Jetpack Compose (BOM 2024.09.00)
- **Architecture**: MVVM with `StateFlow`, `SharingStarted.WhileSubscribed`, and coroutines
- **Database**: AndroidX Room 2.7.0 with KSP (Kotlin Symbol Processing)
- **Navigation**: Jetpack Navigation Compose
- **Build System**: Gradle 9.3+ (Kotlin DSL)

---

## 🚀 Building Without Android Studio

You can build, test, and package this application directly from any terminal on Linux, macOS, or Windows using the included Gradle wrapper scripts.

### Prerequisites
- **JDK 17** installed (`java -version`)
- Git installed

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/smart-notepad.git
cd smart-notepad
```

### 2. Make the Gradle Wrapper Executable (Linux/macOS)
```bash
chmod +x gradlew
```

### 3. Build the Debug APK
```bash
./gradlew assembleDebug
```
The output APK is generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 4. Build the Release APK
```bash
./gradlew assembleRelease
```
The output APK is generated at:
```
app/build/outputs/apk/release/app-release.apk
```

### 5. Install Directly on a Connected Android Device
```bash
# Enable USB Debugging on your device, connect via USB, then run:
./gradlew installDebug
```

### 6. Run Automated Tests
```bash
./gradlew test
```

---

## ⚙️ Automated GitHub Actions CI/CD

This repository includes a production-ready GitHub Actions workflow (`.github/workflows/android-build.yml`).

### Workflow Capabilities
- **Triggers**:
  - Every `push` to `main` or `master` branches
  - Every `pull_request` targeting `main` or `master`
  - Manual trigger on demand via `workflow_dispatch`
- **Steps Executed**:
  1. Clones repository code.
  2. Sets up Temurin JDK 17.
  3. Caches Gradle dependencies for high-speed builds.
  4. Compiles both **Debug APK** and **Release APK**.
  5. Uploads standardized artifacts (`app-debug.apk` and `app-release.apk`).

### How to Download APKs from GitHub Actions

1. Go to your repository on GitHub.
2. Click the **Actions** tab at the top.
3. Click on the latest workflow run (e.g., *"Build Android APK"*).
4. Scroll down to the **Artifacts** section at the bottom of the summary page.
5. Click on **`app-debug`**, **`app-release`**, or **`smart-notepad-apks`** to download the ZIP file containing the installable `.apk`.
6. Transfer the APK to your Android device, enable *"Install Unknown Apps"* in settings, and install!

---

## 🔐 Release Signing & Keystore Configuration

By default, the build script includes an automatic CI fallback: if no custom keystore is configured, it safely signs the release APK using the debug key configuration so your builds succeed out-of-the-box on GitHub Actions.

### Setting Up a Custom Release Keystore

To sign your production APK with your private key:

#### 1. Generate a Keystore File
Run this command in your terminal:
```bash
keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```
Follow the prompts to set passwords for your keystore and alias.

#### 2. Configure GitHub Secrets
In your GitHub repository, go to **Settings** > **Secrets and variables** > **Actions** and add:
- `KEYSTORE_BASE64`: The base64-encoded string of your `my-upload-key.jks` file (encode via `base64 -w 0 my-upload-key.jks`).
- `STORE_PASSWORD`: Password used when generating the keystore.
- `KEY_ALIAS`: Alias name (e.g. `upload`).
- `KEY_PASSWORD`: Key password.

#### 3. Version Code Management
To publish updates, increment `versionCode` and update `versionName` in `app/build.gradle.kts`:
```kotlin
defaultConfig {
    applicationId = "com.smartnotepad.app"
    minSdk = 24
    targetSdk = 36
    versionCode = 2        // Increment for each release
    versionName = "1.1.0"  // Semantic versioning
}
```

---

## 📄 License

```
MIT License

Copyright (c) 2026 Smart Notepad Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
