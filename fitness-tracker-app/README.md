# FitTrack Pro — Android Fitness Trainer App

A native Android application for personal fitness trainers to manage clients, track body measurements over time, and log training sessions with detailed exercise records.

---

## Features

### Client Management
- Add clients with name, age, gender, phone, email, and fitness goal
- Search clients by name
- View a client profile card with total sessions and latest weight
- Edit or delete clients (cascade deletes all data)

### Body Measurements (Date-wise)
- Record measurements on any date using a date picker
- Fields: Weight, Height, Body Fat %, Chest, Waist, Hips, Left/Right Bicep, Left/Right Thigh, Left/Right Calf, Notes
- View measurement history sorted newest → oldest per client
- Delete individual measurement records

### Training Sessions
- Create sessions with a **date picker** and **body part selector** (chip group):
  - Chest · Back · Legs · Shoulders · Arms · Core · Full Body · Cardio
- Optional duration (minutes) and session notes
- **Exercise tracking** per session:
  - Exercise name (with autocomplete from history)
  - Sets · Reps · Weight (kg) — 0 kg = bodyweight
  - Add / remove exercises dynamically
- View session detail with full exercise list
- Edit or delete sessions

---

## Architecture

```
MVVM + Room + Navigation Component
├── data/
│   ├── database/
│   │   ├── entities/     Client, Measurement, Session, Exercise
│   │   ├── dao/          ClientDao, MeasurementDao, SessionDao, ExerciseDao
│   │   └── AppDatabase   Room singleton
│   └── repository/       FitnessRepository
├── ui/
│   ├── clients/          ClientListFragment, AddEditClientFragment, ClientDetailFragment
│   ├── measurements/     AddEditMeasurementFragment, MeasurementAdapter
│   └── sessions/         AddEditSessionFragment, SessionDetailFragment, adapters
└── utils/                DateUtils
```

## Tech Stack
- **Language**: Kotlin
- **Database**: Room (SQLite)
- **Architecture**: MVVM — ViewModel + LiveData + Repository
- **Navigation**: Jetpack Navigation Component
- **UI**: Material Design 3 — Cards, Chips, FAB, TextInputLayout, RecyclerView
- **Async**: Kotlin Coroutines
- **View binding**: ViewBinding

---

## Setup

1. Open `fitness-tracker-app/` in Android Studio (Electric Eel or newer)
2. Let Gradle sync
3. Run on emulator (API 26+) or physical device

### Requirements
- Android Studio Hedgehog or newer
- JDK 17
- compileSdk 34 / minSdk 26

---

## Screens

| Screen | Description |
|--------|-------------|
| Client List | Searchable list of all clients with avatar and goal chip |
| Add/Edit Client | Form: name, age, gender, phone, email, goal |
| Client Detail | Profile header + Sessions tab + Measurements tab |
| Add Session | Date picker, body-part chip, duration, notes + dynamic exercise rows |
| Session Detail | Full exercise list with sets × reps and weight |
| Add Measurement | Full body measurement form with date picker |
