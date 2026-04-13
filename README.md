# NutriLab

NutriLab is a mobile app designed to help users track nutrition, manage meals, and build healthier habits — all from their Android device.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Android Studio | IDE and development environment |
| Firebase Firestore | Database |
| GPT-3.5 Turbo (API key) | NutriBot AI chatbot |
| Open Food Facts API | Free food nutrition data |
| Google Cloud | Hosting and backend services |
| GitHub | Version control and collaboration |

---

## Prerequisites

Before getting started, make sure you have the following installed:

- [Java JDK](https://www.oracle.com/java/technologies/downloads/)
- [Android SDK](https://developer.android.com/studio)
- [Android Studio](https://developer.android.com/studio) (recommended) — includes the emulator and SDK tools
- A **Pixel 5 device emulator** (set up via Android Studio's AVD Manager)

---

## Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd nutrilab
```

### 2. Compile the App

From the project root directory, run the appropriate command for your operating system:

**macOS / Linux:**
```bash
./gradlew assembleDebug
```

**Windows:**
```bash
./gradlew.bat assembleDebug
```

### 3. Start an Emulator

Open Android Studio and launch a **Pixel 5** emulator via the AVD Manager, or start one from the command line using your Android SDK tools.

> A Pixel 5 emulator is recommended for the best experience.

### 4. Install & Run the App

Once the emulator is running, install the app onto it:

```bash
./gradlew installDebug
```

The app will launch automatically on the emulator.

---

## Troubleshooting

- **Build fails?** Make sure your `JAVA_HOME` and `ANDROID_HOME` environment variables are set correctly.
- **Emulator not detected?** Confirm the emulator is fully booted before running `installDebug`.
- **Gradle issues?** Try running `./gradlew clean` before rebuilding.

---

## Contributing

Have a bug fix or feature idea? Feel free to open an issue or submit a pull request. Please make sure your code compiles and runs on the Pixel 5 emulator before submitting.

---

## License

This project is licensed under the [MIT License](LICENSE).