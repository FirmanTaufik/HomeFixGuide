# HomeFixGuide

**HomeFixGuide** adalah aplikasi panduan perbaikan dan perakitan peralatan rumah tangga yang dibangun menggunakan teknologi Android modern dan **Kotlin Multiplatform (KMP)**.

---

## 🏗️ Arsitektur Project

Project ini terbagi menjadi beberapa modul utama:

```text
HomeFixGuide/
├── app/          # Modul aplikasi Android (Jetpack Compose UI, Hilt, Navigation, ViewModel)
├── core-api/     # Modul Kotlin Multiplatform (KMP) untuk API, Ktor Client, & Data Scraping/Parsing
└── core-module/  # Modul pustaka / utilitas umum Android
```

---

## 🌐 Kotlin Multiplatform (KMP) - Modul `:core-api`

Modul `:core-api` merupakan modul **Kotlin Multiplatform (KMP)** yang dirancang agar logika pencarian, pengambil data, dan parsing panduan perbaikan (`GuideCase`) dapat dibagikan antara platform **Android** dan **iOS**.

### Platform Target yang Didukung:
- **Android**: `androidLibrary`
- **iOS Device**: `iosArm64`
- **iOS Simulator**: `iosSimulatorArm64`
- **iOS Simulator Intel (Mac x86_64)**: `iosX64`

### Pustaka & Teknologi Utama di `:core-api`:
- **Ktor Client**:
  - `commonMain`: `ktor-client-core`, `ktor-client-content-negotiation`, `ktor-serialization-kotlinx-json`
  - `androidMain`: `ktor-client-okhttp`
  - `iosMain`: `ktor-client-darwin`
- **Ksoup**: Scraping dan parsing HTML pada `commonMain`.
- **Kotlinx Coroutines & Serialization**: Pengolahan data asinkron dan JSON decoding.

---

## 📦 Mengexport XCFramework untuk Xcode (iOS)

Modul `:core-api` dikonfigurasi untuk mengeksport **XCFramework** dengan nama **`CoreApiKit`** menggunakan pustaka KMP `XCFramework`.

### Perintah Export Gradle

Buka terminal di direktori root project dan jalankan perintah berikut:

#### 1. Mode Debug
```bash
./gradlew :core-api:assembleCoreApiKitDebugXCFramework
```
*Hasil output XCFramework akan dibuat di lokasi:*
```text
core-api/build/XCFrameworks/debug/CoreApiKit.xcframework
```

#### 2. Mode Release
```bash
./gradlew :core-api:assembleCoreApiKitReleaseXCFramework
```
*Hasil output XCFramework akan dibuat di lokasi:*
```text
core-api/build/XCFrameworks/release/CoreApiKit.xcframework
```

---

## 🛠️ Cara Integrasi & Menggunakan XCFramework di Xcode

Setelah Anda menjalankan perintah export di atas, ikuti langkah-langkah berikut untuk mengintegrasikannya ke proyek iOS Anda di Xcode:

### 1. Menambahkan `CoreApiKit.xcframework` ke Xcode
1. Buka proyek iOS Anda di **Xcode**.
2. Pilih project target di bagian kiri atas.
3. Buka tab **General**.
4. Scroll ke bawah sampai ke bagian **Frameworks, Libraries, and Embedded Content**.
5. Drag and drop file `CoreApiKit.xcframework` (dari folder `core-api/build/XCFrameworks/debug/` atau `release/`) ke dalam daftar tersebut.
6. Pastikan opsi **Embed** diset menjadi **Embed & Sign**.

### 2. Menggunakan `CoreApiKit` dalam Kode Swift

Setelah framework ditambahkan, Anda dapat melakukan `import CoreApiKit` di file Swift Anda:

```swift
import SwiftUI
import CoreApiKit

class GuideViewModel: ObservableObject {
    private let guideCase = GuideCase()
    @Published var categories: [GuideCategory] = []
    @Published var isLoading = false
    
    func fetchGuides() {
        isLoading = true
        guideCase.getGuides { pairResult, error in
            DispatchQueue.main.async {
                self.isLoading = false
                if let pair = pairResult {
                    // pair.first mengembalikan List<GuideCategory>
                    self.categories = pair.first as? [GuideCategory] ?? []
                } else if let error = error {
                    print("Error fetching guides: \(error.localizedDescription)")
                }
            }
        }
    }
}
```

> **Catatan Async/Await (Swift 5.5+):**
> Fungsi `suspend` di Kotlin KMP secara otomatis diexport sebagai fungsi dengan *completion handler* di Swift, atau dapat dipanggil langsung menggunakan `async/await` di Swift 5.5+.

---

## 🚀 Membuka & Menjalankan Aplikasi Android

1. Buka folder project ini di **Android Studio** (Disarankan Android Studio Ladybug atau yang lebih baru).
2. Sync Gradle project.
3. Jalankan aplikasi pada Emulator / Perangkat Android dengan memilih konfigurasi **`app`**.

Atau melalui terminal:
```bash
./gradlew assembleDebug
```
