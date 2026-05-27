# English Learning App - Android

Ứng dụng Android học tiếng Anh được xây dựng bằng **Kotlin**, **Jetpack Compose**, **Material 3**, **Retrofit**, **Room**, **DataStore** và tích hợp **Google Gemini AI**.  
Ứng dụng kết nối với backend FastAPI để xử lý đăng ký, đăng nhập, bài học, từ vựng, tiến độ học tập và các chức năng luyện tiếng Anh.

---

## Demo

[👉 Preview the English Learning App](https://youtu.be/CzOTT6SSZwo)

---

## Mục lục

- [Giới thiệu](#giới-thiệu)
- [Tính năng chính](#tính-năng-chính)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Kiến trúc project](#kiến-trúc-project)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt và chạy project](#cài-đặt-và-chạy-project)
- [Cấu hình Backend API](#cấu-hình-backend-api)
- [Cấu hình Gemini API Key](#cấu-hình-gemini-api-key)
- [Build APK](#build-apk)
- [Tối ưu hiệu năng](#tối-ưu-hiệu-năng)
- [Troubleshooting](#troubleshooting)
- [Backend liên quan](#backend-liên-quan)

---

## Giới thiệu

English Learning App là ứng dụng Android hỗ trợ người dùng học tiếng Anh thông qua các bài học, từ vựng, thống kê tiến độ và các tính năng luyện tập với AI.

Ứng dụng có giao diện hiện đại bằng Jetpack Compose, hỗ trợ đăng nhập/đăng ký, lưu token người dùng, gọi API backend bằng Retrofit và lưu dữ liệu cục bộ bằng Room/DataStore.

Repo này là phần **mobile app**. Phần backend FastAPI nằm ở repo riêng:

```text
https://github.com/NAHao2401/English-Learning-App-Backend
```

---

## Tính năng chính

### Authentication

- Đăng ký tài khoản mới.
- Đăng nhập bằng email và mật khẩu.
- Đăng nhập Google.
- Lưu access token bằng DataStore.
- Tự động gửi token trong request thông qua interceptor.
- Đổi mật khẩu người dùng.

### Home Dashboard

- Hiển thị lời chào người dùng.
- Hiển thị level hiện tại.
- Hiển thị tổng XP.
- Hiển thị streak.
- Hiển thị số bài đã hoàn thành.
- Hiển thị phần trăm tiến độ.
- Giao diện dạng card hiện đại với Material 3.

### Lessons

- Xem danh sách bài học.
- Học theo chủ đề.
- Tiếp tục bài học đang học.
- Theo dõi số bài đã hoàn thành và bài còn lại.

### Vocabulary

- Học từ vựng theo chủ đề.
- Lọc/tìm kiếm từ vựng.
- Lưu từ vựng yêu thích.
- Hỗ trợ dữ liệu từ backend.
- Tối ưu hiệu năng khi danh sách từ vựng/chủ đề lớn.

### Progress

- Xem thống kê tiến độ học tập.
- Theo dõi XP, streak và lesson progress.
- Hỗ trợ đồng bộ dữ liệu từ backend.

### AI Features

- AI Chat để luyện tập tiếng Anh.
- AI Scan dùng camera hoặc hình ảnh.
- Tích hợp Google Gemini thông qua `GEMINI_API_KEY`.

### Speaking Practice

- Có quyền dùng microphone.
- Chuẩn bị cho chức năng luyện nói, phát âm hoặc ghi âm.

---

## Công nghệ sử dụng

| Nhóm | Công nghệ |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, Repository Pattern |
| Network | Retrofit, OkHttp, Gson Converter |
| Local Storage | Room, DataStore Preferences |
| Dependency Injection | Hilt |
| Async | Kotlin Coroutines |
| Navigation | Navigation Compose |
| Image Loading | Coil Compose |
| AI | Google Generative AI / Gemini |
| Authentication | Token-based auth, Google Sign-In |
| Build Tool | Gradle Kotlin DSL |

---

## Kiến trúc project

Project được tổ chức theo hướng tách lớp rõ ràng:

```text
UI Layer
│
├── Jetpack Compose Screens
├── ViewModel
│
Domain / Data Layer
│
├── Repository
├── Remote API Service
├── Local Database
├── DataStore
│
Backend API
```

Luồng hoạt động cơ bản:

```text
Screen → ViewModel → Repository → Retrofit API / Room / DataStore
```

---

## Cấu trúc thư mục

```text
English-Learning-App/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/englishlearningapp/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── datastore/
│   │   │   │   │   │   └── db/
│   │   │   │   │   │
│   │   │   │   │   ├── remote/
│   │   │   │   │   │   └── api/
│   │   │   │   │   │       ├── AuthApiService.kt
│   │   │   │   │   │       ├── RetrofitClient.kt
│   │   │   │   │   │       ├── request/
│   │   │   │   │   │       └── response/
│   │   │   │   │   │
│   │   │   │   │   └── repository/
│   │   │   │   │
│   │   │   │   ├── features/
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── ui/
│   │   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   │   └── RegisterScreen.kt
│   │   │   │   │   │   └── viewmodel/
│   │   │   │   │   │       ├── AuthUiState.kt
│   │   │   │   │   │       ├── AuthViewModel.kt
│   │   │   │   │   │       └── AuthViewModelFactory.kt
│   │   │   │   │   │
│   │   │   │   │   └── home/
│   │   │   │   │       └── ui/
│   │   │   │   │           └── HomeScreen.kt
│   │   │   │   │
│   │   │   │   ├── ui/theme/
│   │   │   │   └── MainActivity.kt
│   │   │   │
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   ├── androidTest/
│   │   └── test/
│   │
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── gradle/
│   └── libs.versions.toml
│
├── DEPLOYMENT_CHECKLIST.md
├── OPTIMIZATION_PATTERNS.md
├── OPTIMIZATION_SUMMARY.md
├── PERFORMANCE_OPTIMIZATIONS.md
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
└── README.md
```

---

## Yêu cầu hệ thống

Trước khi chạy project, cần cài đặt:

- Android Studio phiên bản mới.
- JDK 11 trở lên.
- Android SDK.
- Gradle wrapper có sẵn trong project.
- Thiết bị Android thật hoặc Android Emulator.
- Backend FastAPI đang chạy ở port `8000`.

Cấu hình Android hiện tại:

| Thuộc tính | Giá trị |
|---|---|
| `applicationId` | `com.example.englishlearningapp` |
| `minSdk` | 24 |
| `targetSdk` | 36 |
| `compileSdk` | 36 |
| `versionCode` | 1 |
| `versionName` | 1.0 |

---

## Cài đặt và chạy project

### 1. Clone repository

```bash
git clone https://github.com/NAHao2401/English-Learning-App.git
cd English-Learning-App
```

### 2. Mở bằng Android Studio

Mở Android Studio và chọn:

```text
File → Open → English-Learning-App
```

Đợi Gradle sync hoàn tất.

### 3. Cấu hình `local.properties`

Trong thư mục gốc project, tạo hoặc mở file `local.properties`.

Thêm Gemini API key:

```properties
GEMINI_API_KEY=your_gemini_api_key_here
```

File `local.properties` thường cũng có đường dẫn SDK, ví dụ:

```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
GEMINI_API_KEY=your_gemini_api_key_here
```

Không nên commit `local.properties` lên GitHub vì file này có thể chứa API key cá nhân.

### 4. Chạy backend

Clone và chạy backend trước:

```bash
git clone https://github.com/NAHao2401/English-Learning-App-Backend.git
cd English-Learning-App-Backend
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### 5. Chạy Android app

Trong Android Studio:

```text
Chọn emulator hoặc thiết bị thật → Run app
```

Hoặc chạy bằng terminal:

```bash
./gradlew assembleDebug
```

Trên Windows:

```bash
gradlew.bat assembleDebug
```

---

## Cấu hình Backend API

Trong app, `RetrofitClient` đang dùng base URL:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8000/"
```

### Khi chạy bằng Android Emulator

Giữ nguyên:

```text
http://10.0.2.2:8000/
```

`10.0.2.2` là địa chỉ để emulator truy cập localhost của máy tính.

### Khi chạy bằng điện thoại thật cùng Wi-Fi

Đổi `BASE_URL` thành IP LAN của máy tính, ví dụ:

```kotlin
private const val BASE_URL = "http://192.168.1.10:8000/"
```

Sau đó chạy backend với:

```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

### Khi chạy bằng điện thoại thật qua USB

Có thể dùng ADB reverse:

```bash
adb devices
adb reverse tcp:8000 tcp:8000
```

Sau đó đổi base URL thành:

```kotlin
private const val BASE_URL = "http://127.0.0.1:8000/"
```

---

## Cấu hình Gemini API Key

Project đọc Gemini API key từ `local.properties` và đưa vào `BuildConfig`:

```kotlin
buildConfigField("String", "GEMINI_API_KEY", "\"${properties["GEMINI_API_KEY"]}\"")
```

Vì vậy cần thêm dòng sau vào `local.properties`:

```properties
GEMINI_API_KEY=your_gemini_api_key_here
```

Nếu không dùng chức năng AI, bạn vẫn nên để giá trị rỗng hoặc dummy để tránh lỗi build:

```properties
GEMINI_API_KEY=
```

---

## Quyền Android được sử dụng

Project khai báo các quyền sau trong `AndroidManifest.xml`:

| Permission | Mục đích |
|---|---|
| `INTERNET` | Gọi API backend và AI service |
| `RECORD_AUDIO` | Luyện nói hoặc ghi âm |
| `CAMERA` | AI Scan hoặc chụp ảnh |
| `READ_MEDIA_IMAGES` | Đọc ảnh trên Android 13+ |
| `READ_EXTERNAL_STORAGE` | Fallback cho Android dưới 13 |

App cũng bật:

```xml
android:usesCleartextTraffic="true"
```

Điều này cho phép gọi HTTP local backend trong quá trình phát triển.

---

## API app đang gọi

### Auth API

| Method | Endpoint | Mô tả |
|---|---|---|
| `POST` | `/auth/register` | Đăng ký tài khoản |
| `POST` | `/auth/login` | Đăng nhập |
| `POST` | `/auth/google` | Đăng nhập Google |
| `PUT` | `/auth/me/password` | Đổi mật khẩu |

### Các API khác

App cũng có service riêng cho:

- Vocabulary API
- Lesson API
- Progress API

Các API này tương ứng với backend FastAPI của project English Learning App Backend.

---

## Build APK

### Debug APK

```bash
./gradlew assembleDebug
```

File APK sẽ nằm tại:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK

```bash
./gradlew assembleRelease
```

File APK release sẽ nằm tại:

```text
app/build/outputs/apk/release/app-release.apk
```

Nếu muốn release thật, cần cấu hình signing key trong Gradle.

---

## Tối ưu hiệu năng

Repo có các tài liệu riêng về tối ưu hiệu năng:

- `PERFORMANCE_OPTIMIZATIONS.md`
- `OPTIMIZATION_SUMMARY.md`
- `OPTIMIZATION_PATTERNS.md`
- `DEPLOYMENT_CHECKLIST.md`

Một số tối ưu đã được áp dụng cho Vocabulary tab:

- Pagination, ban đầu chỉ render 8 topics.
- Thêm `contentType` cho LazyRow/LazyColumn để tái sử dụng item tốt hơn.
- Dùng stable keys cho lazy list.
- Giảm tính toán gradient nặng.
- Memoize các giá trị tính toán bằng `remember`.
- Thêm nút “Load More” để tải thêm topic theo nhu cầu.
- Giảm thời gian render ban đầu và cải thiện độ mượt khi scroll.

---

## Troubleshooting

| Lỗi | Nguyên nhân có thể | Cách xử lý |
|---|---|---|
| `GEMINI_API_KEY` null hoặc lỗi build | Chưa thêm key vào `local.properties` | Thêm `GEMINI_API_KEY=...` |
| App không gọi được backend trên emulator | Sai base URL | Dùng `http://10.0.2.2:8000/` |
| App không gọi được backend trên điện thoại thật | Điện thoại không truy cập được localhost máy tính | Dùng IP LAN hoặc `adb reverse` |
| `Cleartext HTTP traffic not permitted` | Chưa cho phép HTTP | Kiểm tra `usesCleartextTraffic="true"` |
| Đăng nhập thất bại | Backend chưa chạy hoặc sai tài khoản | Chạy backend và kiểm tra `/docs` |
| Gradle sync lỗi | Phiên bản Android Studio/JDK không phù hợp | Cập nhật Android Studio và dùng JDK 11+ |
| Không dùng được camera/micro | Chưa cấp quyền runtime | Cấp quyền trong Android settings hoặc xử lý permission trong app |

---

## Backend liên quan

Backend repository:

```text
https://github.com/NAHao2401/English-Learning-App-Backend
```

Backend cung cấp:

- Auth API.
- Lesson API.
- Vocabulary API.
- Progress API.
- JWT authentication.
- PostgreSQL database.
- Swagger UI tại `/docs`.

---

## Gợi ý phát triển tiếp

- Thêm màn hình chi tiết lesson và quiz flow hoàn chỉnh.
- Thêm màn hình profile người dùng.
- Thêm refresh token.
- Tách `BASE_URL` theo build variant: debug/release.
- Thêm Hilt injection đầy đủ cho Repository, Retrofit và ViewModel.
- Thêm Paging 3 cho danh sách vocabulary/topic lớn.
- Thêm unit test cho Repository và ViewModel.
- Thêm CI bằng GitHub Actions.
- Thêm ảnh screenshot app vào README.
- Cấu hình release signing để build APK production.
