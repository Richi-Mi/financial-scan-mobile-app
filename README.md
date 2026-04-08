# Financial Scan 🧾📱

**Financial Scan** is a modern Android application designed to help users manage their personal finances by scanning and extracting information from receipts and tickets using On-Device AI.

## 🚀 Features

- **Smart Scanning**: Capture receipts using CameraX and extract text with Google ML Kit OCR.
- **On-Device AI**: Utilizes **Google AI Edge (Gemma)** to process and categorize financial data locally, ensuring privacy and speed.
- **Expense History**: Keep track of all your scanned documents in a local database.
- **Background Reminders**: Automated notifications using WorkManager to remind users to log their expenses.
- **Modern UI**: Built entirely with Jetpack Compose following Material 3 design guidelines.

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection**: [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **AI/ML**: 
    - [Google ML Kit OCR](https://developers.google.com/ml-kit/vision/text-recognition)
    - [Google AI Edge (Gemma)](https://ai.google.dev/edge)
- **Background Tasks**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Navigation**: [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- **Image Loading**: [Coil](https://coil-api.github.io/coil/)

## 📂 Project Structure

The project follows Clean Architecture principles:

- `data/`: Implementation of repositories, Room entities, DAOs, and Retrofit API services.
- `domain/`: Business logic and repository interfaces.
- `ui/`: UI layer containing:
    - `presentation/`: Screen-specific Composable functions and ViewModels (Home, History, Scanner, etc.).
    - `components/`: Reusable UI elements.
    - `navigation/`: Navigation graphs and wrappers.
    - `theme/`: Material 3 theme configuration.
- `di/`: Dependency injection modules.
- `worker/`: Background workers for notifications and reminders.

## ⚙️ Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/financial-scan-mobile-app.git
   ```
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Sync Gradle and build the project.
4. Run the app on a physical device or emulator (API 24+).

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
