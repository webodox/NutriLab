# NutriLab

NutriLab is a mobile app designed to help users track nutrition, manage meals, and build healthier habits — all from their Android device.

## Team
**Group 7 — Nutrient Knights | Spring 2026
Name(s): Kobie Brown | Kenny Bui | Dara Dawodu | Mati Sawadogo | Nour Khoulani  |

---
## Key Features
- User Authentication — secure login, registration, and password reset via Firebase
- Meal Tracking — log meals by searching real foods or manually entering nutrition data
- Food Search — search millions of real foods using the Open Food Facts API
- NutriBot AI — OpenAI GPT-3.5 powered chatbot for meal plans and nutrition advice
- Water Intake Tracking — daily hydration tracking with progress bar toward 64oz goal
- Symptom Tracking — log daily health symptoms saved to Firebase
- Notification Reminders — schedule daily reminders for meals, water, and symptoms
- BMI Calculator — calculate BMI and daily calorie needs
- Custom Meal Plans — create, edit, and delete personal meal plans
- Tracking Progress — calendar view of logged meals and symptoms by date
- User Achievements — earn points and badges for logging meals
- Submit Feedback — send feedback directly from the app
- Data Export — download health data as a .txt file
- Dietary Restrictions & Allergies — set allergies in profile, NutriBot automatically avoids them
- Profile Management — edit personal information and nutrition goals


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


## Configuration
This app requires an OpenAI API key for the NutriBot chatbot feature.

1. Get an API key from [platform.openai.com](https://platform.openai.com)
2. Open `local.properties` in the project root
3. Add the following line:

```
OPENAI_API_KEY=your-api-key-here
```

The Open Food Facts API requires no key and works out of the box.

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

## Dependencies
- `androidx.room` — local database
- `kotlinx-coroutines-android` — async operations
- `firebase-bom` — Firebase services
- `firebase-firestore` — cloud database
- `firebase-auth` — authentication
- `androidx.cardview` — UI card components



## Troubleshooting

- **Build fails?** Make sure your `JAVA_HOME` and `ANDROID_HOME` environment variables are set correctly.
- **Emulator not detected?** Confirm the emulator is fully booted before running `installDebug`.
- **Gradle issues?** Try running `./gradlew clean` before rebuilding.
- **Chatbot not working?** Make sure your OpenAI API key is added to `local.properties`.

---

## Contributing

Have a bug fix or feature idea? Feel free to open an issue or submit a pull request. Please make sure your code compiles and runs on the Pixel 5 emulator before submitting.

---

## License

This project is licensed under the [MIT License](LICENSE).
