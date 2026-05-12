# Sprout

> A calm, personal houseplant tracker for Android. No cloud. No accounts. Just your plants and their schedules.

Sprout helps you track your houseplants and reminds you when each one needs water. Record a plant's care needs once, tap a button when you water it, and the app handles the rest with local notifications.

---

## Screenshots

| Plant List | Plant Detail | Add Plant | Care History |
|:---:|:---:|:---:|:---:|
| *(coming soon)* | *(coming soon)* | *(coming soon)* | *(coming soon)* |

---

## Features

- **Plant list** — see every plant with its watering status at a glance (due today, overdue, or due in N days)
- **Watering reminders** — local push notifications scheduled N days after the last watering; no calendar, no exact-alarm permission
- **Care log** — record waterings, fertilizing, pH measurements, repottings, and free-text notes
- **Fertilizer schedule** — optional interval-based fertilizer tracking alongside watering
- **Add / edit / delete plants** — take or pick a photo, set a name and intervals; soft-delete preserves history
- **Care history** — full chronological event log per plant, filterable by event type
- **100% on-device** — no internet required, no accounts, no sync, no telemetry

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM / UDF, `StateFlow<UiState>` |
| DI | Hilt |
| Database | Room (SQLite) |
| Concurrency | Coroutines + Flow |
| Background / notifications | WorkManager |
| Image loading | Coil |

---

## Getting Started

**Requirements**

- Android Studio Hedgehog or later
- JDK 17
- Android device or emulator running API 26+ (Android 8.0)

**Build**

```bash
git clone https://github.com/galen-f/sprout.git
cd sprout
./gradlew assembleDebug
```

Install on a connected device:

```bash
./gradlew installDebug
```

---

## Permissions

Sprout requests only what it needs:

| Permission | When | Why |
|---|---|---|
| `POST_NOTIFICATIONS` | Runtime, Android 13+ | Watering and fertilizer reminders |
| `CAMERA` | Runtime, when user taps "Take photo" | Optional plant photo; the gallery picker works without it |

No internet permission is requested or used.

---

## Project Structure

```
com.example.sprout
├── data
│   ├── db            # Room database, DAOs, entity↔domain mappers
│   ├── photo         # PhotoStorage: saves/loads JPEGs from filesDir
│   └── repository    # PlantsRepository, CareEventsRepository
├── domain
│   ├── model         # Plant, CareEvent, WateringStatus (pure Kotlin)
│   └── usecase       # LogWateringUseCase, ScheduleNextReminderUseCase, etc.
├── notifications
│   ├── ReminderScheduler   # wraps WorkManager
│   ├── ReminderWorker      # CoroutineWorker that posts the notification
│   └── NotificationChannels
└── ui
    ├── theme         # Color, Gradients, Typography, Shape, Theme
    ├── components    # PlantCard, StatusPill, WaterButton, GradientBackground
    ├── plantlist
    ├── plantdetail
    ├── addplant
    ├── editplant
    ├── carehistory
    └── settings
```

---

## License

[MIT](LICENSE)
