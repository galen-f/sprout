# Sprout — Houseplant Tracker

> A personal, on-device Android app for tracking houseplants and getting nudged when they need water.

This document is the source of truth for the app's design.

---

## 1. Product overview

### 1.1 Purpose
Sprout helps a single user track their houseplants and, most importantly, **reminds them to water each plant on its own schedule**. The user records each plant's care needs once, taps a button when they water or fertilize, and the app handles the rest with local notifications.

### 1.2 Scope (what the app *is*)
- A personal, single-user Android app.
- A local plant log: name, photo, watering interval, fertilizer schedule, soil pH, free-form notes, care history.
- A local reminder engine that schedules a notification N days after the last watering.
- A simple, calm UI with warm earth and green tones.

### 1.3 Anti-scope (what the app is explicitly *not*)
- **No cloud backend.** No AWS, Firebase, Supabase, or any remote server. All data lives on-device.
- **No accounts, no auth, no sync.** It's a single-user app for the device owner.
- **No AI / ML.** No plant-ID from photos, no health-diagnosis from images, no model inference of any kind.
- **No social / sharing features.**
- **No telemetry or analytics.** Nothing leaves the device.
- **No internet permission required at all** for core functionality (see §7).

### 1.4 Primary user story
> As the owner of several houseplants, I want to open the app, see which plants need water today, tap a plant to confirm I just watered it, and trust that the app will quietly remind me again in N days.

---

## 2. Core concepts

### 2.1 Plant
A single houseplant in the user's collection. Has a profile (name, photo) and a set of care rules (watering interval, fertilizer interval, etc.) plus a log of care events.

### 2.2 Care event
An immutable record of something the user did to a plant: watered it, fertilized it, repotted it, measured pH, or wrote a note. Each event has a timestamp, a type, and optional payload (e.g. pH value, fertilizer name, free-text note).

### 2.3 Watering schedule
A simple interval in days, attached to a plant. When the user logs a "watered" event, the app schedules a notification for `now + intervalDays`. There is no calendar, no weekly pattern — just "every N days from the last watering." This intentionally matches how plant care actually works: a plant watered late should be due N days from when it was actually watered, not from a fixed weekly slot.

### 2.4 Fertilizer schedule
Same model as watering: an interval in days, a "last fertilized" timestamp, and the type of fertilizer being used (free-text). Notifications for fertilizing are lower priority than watering (see §6.3).

---

## 3. Feature list

### 3.1 MVP (must ship)
1. **Plant list screen** showing every plant with thumbnail, name, and "due in X days" / "due today" / "overdue by X days" status.
2. **Add plant** flow: take or pick a photo, enter a name, set watering interval (days).
3. **Plant detail / profile screen** with photo, name, schedules, and full care history.
4. **"I just watered it" button** on the detail screen that records the event and (re)schedules the next reminder.
5. **Local push notifications** for due waterings, with a tap-through into the plant detail.
6. **Edit plant** (name, photo, intervals) and **delete plant**.
7. **Care log entries** for: watering, fertilizing, pH measurement, free-text note.

### 3.2 Nice-to-have (post-MVP)
- "Snooze 1 day" action on the notification.
- Multiple photos per plant (gallery, with the most recent as the cover).
- Export / import the local database (JSON dump to user-chosen file via Storage Access Framework).
- A simple home-screen widget showing "plants due today."
- Light / dark theme toggle (system-follow by default).

### 3.3 Explicitly deferred or rejected
- Plant species identification from photos.
- Disease diagnosis from photos.
- Recommended care schedules looked up from a remote database.
- Multi-device sync.

---

## 4. Visual design

### 4.1 Mood
Warm, earthy, slightly damp. Think "forrest floor after a long warm rainstorm." The user should feel calm opening the app, not productive-app-tense.

### 4.2 Palette
All colors shall live in `ui/theme/Color.kt`. Both light and dark schemes are defined; system theme is followed by default.

**Light theme**

| Role | Token | Hex | Notes |
|---|---|---|---|
| Primary (leaf) | `LeafGreen` | `#4A6B3A` | Buttons, primary accents |
| Primary container | `MossGreen` | `#7A9B5E` | Selected states, chips |
| Secondary (earth) | `EarthBrown` | `#6B4423` | Secondary actions, icons |
| Secondary container | `Terracotta` | `#A66B4A` | Pot illustrations, highlights |
| Background | `WarmCream` | `#F4EDE0` | App background |
| Surface | `WarmSand` | `#EFE3D0` | Cards, sheets |
| Surface variant | `DampSoil` | `#D9C8AC` | Dividers, inactive |
| On-surface | `DeepBark` | `#3A2A1F` | Body text |
| Accent (water) | `MistBlue` | `#6B8A8A` | Watering button, water drops |
| Error | `RipeBerry` | `#8C3A2E` | Overdue states |

**Dark theme** is the same palette dimmed and desaturated against a `DeepSoil #2A2017` background, with `LeafGreen` lightened to `#8FB073` for contrast.

### 4.3 Gradients ("moist" feeling)
The hero areas of the app — the plant list header and the plant profile photo backdrop — use soft vertical gradients to feel humid and alive rather than flat.

- **`SoilGradient`**: top `WarmSand #EFE3D0` → bottom `DampSoil #D9C8AC`. Used behind the app bar and on empty-state surfaces.
- **`CanopyGradient`**: top `MossGreen #7A9B5E` → bottom `LeafGreen #4A6B3A`. Used on the primary "I watered it" button and the "due today" status pill.
- **`DewGradient`**: top `MistBlue #6B8A8A` at 60% opacity → bottom transparent. Used as a thin overlay on plant photos to suggest condensation/humidity.

Gradients are defined once as `Brush` objects in `ui/theme/Gradients.kt` and referenced by name everywhere else. Never inline a gradient at a call site.

### 4.4 Typography
- **Display / headings**: a humanist serif (e.g. *Fraunces* or *Lora* via `androidx.compose.ui.text.googlefonts`). Slightly soft, organic.
- **Body / UI**: a clean geometric sans (e.g. *Nunito* or system default). Readable, friendly.
- Heading sizes: `displaySmall` 32sp, `headlineMedium` 24sp, `titleLarge` 20sp. Body 16sp. Captions 13sp.

### 4.5 Shape & motion
- Corner radius: 16dp for cards, 24dp for sheets, 999dp (pill) for primary CTAs.
- Elevation: light. Prefer soft shadows (4–6 dp) and tonal surface variants over hard drop shadows.
- Motion: gentle. `tween(300, easing = FastOutSlowInEasing)` for most transitions. The "Watered" button does a small spring/scale animation on tap and a brief water-drop ripple.

---

## 5. Information architecture

```
PlantListScreen           (start destination)
 ├── AddPlantScreen       (FAB → form + camera/picker)
 └── PlantDetailScreen    (tap a plant card)
      ├── EditPlantScreen
      ├── LogCareEventSheet   (modal bottom sheet: water | fertilize | pH | note)
      └── CareHistoryScreen   (chronological event list, filterable by type)
SettingsScreen            (overflow menu from PlantListScreen)
```

Navigation uses **Jetpack Navigation Compose** with a single `NavHost`. All screens are top-level destinations except `LogCareEventSheet`, which is a `ModalBottomSheet`.

---

## 6. Domain model

### 6.1 Entities

```kotlin
// Persisted as a Room entity.
data class Plant(
    val id: Long,                       // auto-generated
    val name: String,
    val photoPath: String?,             // absolute path to the JPEG in app-internal storage
    val wateringIntervalDays: Int,      // e.g. 7
    val lastWateredAt: Instant?,        // null = never watered through the app
    val fertilizerIntervalDays: Int?,   // null = no fertilizer schedule
    val lastFertilizedAt: Instant?,
    val fertilizerType: String?,        // free text, e.g. "Miracle-Gro 10-10-10"
    val createdAt: Instant,
    val archivedAt: Instant?            // soft-delete; archived plants are hidden but not lost
)

enum class CareEventType { WATERED, FERTILIZED, PH_MEASURED, REPOTTED, NOTE }

data class CareEvent(
    val id: Long,
    val plantId: Long,
    val type: CareEventType,
    val occurredAt: Instant,
    val phValue: Double?,               // only for PH_MEASURED
    val fertilizerType: String?,        // only for FERTILIZED
    val note: String?                   // free text, used by NOTE and optionally attached to any event
)
```

### 6.2 Derived state (not persisted)
- `wateringDueAt = lastWateredAt + wateringIntervalDays.days`
- `wateringStatus`: `NeverWatered | DueIn(days) | DueToday | OverdueBy(days)`
- `fertilizerStatus`: same shape as watering, or `NotScheduled` if `fertilizerIntervalDays` is null.

Computed in the `PlantsRepository` via a pure function `Plant.wateringStatus(now: Instant): WateringStatus` so it can be unit-tested without a clock.

### 6.3 Notification rules
- Watering notifications are scheduled at `wateringDueAt`. Channel: **"Watering" (IMPORTANCE_DEFAULT)**.
- Fertilizer notifications are scheduled at `lastFertilizedAt + fertilizerIntervalDays.days`. Channel: **"Fertilizing" (IMPORTANCE_LOW)** — visible in the tray but no sound.
- Notifications have a deep link back to the plant's detail screen.
- When the user logs a "WATERED" event, any pending watering notification for that plant is cancelled and a new one is scheduled.
- If the device is off or in Doze mode at the scheduled time, the notification fires at the next available wake window (`setExactAndAllowWhileIdle` is **not** used — we don't need minute-level precision and we don't want to ask for `SCHEDULE_EXACT_ALARM`).

---

## 7. Architecture

### 7.1 Stack
- **Language**: Kotlin (target latest stable, currently 2.x).
- **UI**: Jetpack Compose, Material 3.
- **Architecture**: MVVM with a unidirectional data flow. ViewModels expose `StateFlow<UiState>`; the UI is a pure function of state.
- **DI**: Hilt.
- **Persistence**: Room (SQLite).
- **Concurrency**: Kotlin coroutines + Flow.
- **Background work**: `WorkManager` for scheduling and delivering notifications. Chosen over `AlarmManager` because it handles Doze, reboots, and battery optimization correctly without requiring the exact-alarm permission.
- **Images**: Coil for loading. Photos are stored as JPEGs in `context.filesDir/plants/{plantId}/{timestamp}.jpg`.
- **Min SDK**: 26 (Android 8.0). **Target SDK**: latest stable.

### 7.2 Permissions
Only what's strictly needed:
- `POST_NOTIFICATIONS` (runtime, Android 13+) — for reminders.
- `CAMERA` (runtime) — only requested when the user taps "Take photo." If denied, the photo picker still works.

**Not requested**: internet, location, exact alarms.

### 7.3 Module / package layout
A single Gradle module is fine for this size. Internal package structure:

```
com.example.sprout
├── data
│   ├── db            // Room database, DAOs, entity↔domain mappers
│   ├── photo         // PhotoStorage: save/load JPEGs from filesDir
│   └── repository    // PlantsRepository, CareEventsRepository
├── domain
│   ├── model         // Plant, CareEvent, WateringStatus, etc. (pure Kotlin)
│   └── usecase       // LogWateringUseCase, ScheduleNextReminderUseCase, etc.
├── notifications
│   ├── ReminderScheduler         // wraps WorkManager
│   ├── ReminderWorker            // CoroutineWorker that posts the notification
│   └── NotificationChannels      // channel definitions
├── ui
│   ├── theme         // Color.kt, Gradients.kt, Type.kt, Shape.kt, Theme.kt
│   ├── components    // PlantCard, StatusPill, WaterButton, GradientBackground
│   ├── plantlist
│   ├── plantdetail
│   ├── addplant
│   ├── editplant
│   ├── carehistory
│   └── settings
└── SproutApp.kt      // Hilt Application
```

### 7.4 Data layer rules
- The DAO returns Room entities; the repository maps them to/from domain models. The rest of the app **never** sees Room types.
- All repository functions return `Flow<T>` for reads and `suspend fun` for writes.
- The clock is injected as a `Clock` interface (default `Clock.systemUTC()`) so tests can pin time.

### 7.5 ViewModel contract
Every screen has a ViewModel exposing:
```kotlin
val uiState: StateFlow<ScreenUiState>
fun onEvent(event: ScreenEvent)
```
`ScreenUiState` is a sealed interface with `Loading`, `Empty`, `Content(...)`, `Error(message)` variants. Events are a sealed interface of user intents. No `LiveData`, no two-way binding, no direct mutation from the UI.

---

## 8. Key flows

### 8.1 Add plant
1. User taps the FAB on `PlantListScreen` → navigates to `AddPlantScreen`.
2. Form fields: photo (camera or picker), name (required), watering interval in days (required, default 7), optional fertilizer interval and type, optional initial note.
3. On save: insert `Plant` row, copy the photo into `filesDir/plants/{id}/cover.jpg`, navigate back. **No reminder is scheduled yet** — the schedule starts when the user first logs a watering.

### 8.2 Log a watering
1. User taps **"Watered"** on `PlantDetailScreen`.
2. `LogWateringUseCase` runs in a coroutine: insert a `CareEvent(WATERED, now)`, update `Plant.lastWateredAt = now`, cancel any pending watering work for this plant, enqueue a new `ReminderWorker` with `initialDelay = intervalDays`.
3. UI shows a quick confirmation ("Watered ✓ — next reminder in N days") and the status pill on the detail screen flips to "Due in N days."

### 8.3 Reminder fires
1. `ReminderWorker` runs at the scheduled time, reads the latest plant state from the repository (defends against the user having watered manually in the meantime), and only posts the notification if the plant is genuinely due.
2. The notification deep-links to `PlantDetailScreen` with the plant id, where the user can tap "I just watered it" to repeat the cycle.

### 8.4 Edit / delete
- **Edit**: standard form; if the watering interval changes and the plant has a `lastWateredAt`, the pending reminder is cancelled and re-scheduled with the new interval.
- **Delete**: soft-delete by setting `archivedAt`. A confirmation dialog warns this also stops reminders. A "Show archived" toggle in Settings lets the user restore.

---

## 9. Testing strategy
- **Unit tests** for: status-derivation functions, use cases (with a fake `Clock` and fake repository), and the photo-storage helper.
- **Instrumentation tests** for: Room DAOs (in-memory database) and `ReminderWorker` (using `WorkManagerTestInitHelper`).
- **Compose UI tests** for: the watering button's optimistic update and the "overdue" pill rendering.
- Target ≥80% coverage in `domain/` and `data/repository/`. UI coverage is not chased.

---

## 10. Open questions

These are not blockers for starting MVP work, but should be answered before the relevant feature is built. Claude Code should ask before guessing.

1. **Time zones.** Should the reminder fire at a specific time of day (e.g. 9 AM local) rather than exactly N×24h after the last watering? Likely yes — most users want morning reminders. **Tentative answer:** schedule for 9 AM local on the due date.
2. **Multiple photos per plant.** MVP stores one cover photo. When we add a gallery (§3.2), do we want to keep all history photos or cap them?
3. **Backup format.** When export/import lands, is a single JSON file (with base64 photos) acceptable, or do we want a zip with photos as files?
4. **Widget.** Glance API or classic RemoteViews? Glance is the modern choice but pulls more dependencies.

---

## 11. Conventions for Claude Code

When extending this project, please:
- Keep the domain layer pure Kotlin (no Android imports).
- Add new colors and gradients to the theme files; don't inline them.
- Prefer adding a use case over expanding a ViewModel.
- Write unit tests alongside any new use case or pure function.
- If a feature would require network access, a remote service, or any ML model, **stop and ask** — see §1.3.
- Match the existing tone in user-facing strings: warm, calm, brief. No exclamation marks in notifications.
