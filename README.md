# MakerHub

MakerHub is an Android application designed for managing equipment, tracking loan status, borrowers, and checkout histories in a makerspace environment.

---

## Technical Specifications

- **Min SDK**: `35` (Android 15)
- **Target SDK**: `37`
- **Compile SDK**: `37`
- **JDK Version**: Java 11
- **Gradle Version / AGP**: `8.x` / AGP `9.4.1`
- **Language**: Kotlin

---

## Tech Stack & Architecture

MakerHub follows the **MVVM (Model-View-ViewModel)** architectural pattern adhering to Clean Architecture guidelines:

- **Presentation Layer**: Activities with ViewBinding (`ItemListActivity`, `ItemDetailActivity`), Custom Adapters, and `ViewModel`s.
- **Domain Layer**: Clean use cases isolating business rules (`CheckoutItemUseCase`, `ReturnItemUseCase`, `GetItemListUseCase`, `GetItemDetailUseCase`, `ImportDataUseCase`).
- **Data Layer**:
  - **Room Database (KSP)**: Local persistence for items, borrowers, and checkout records.
  - **Repository Pattern**: `LocalRepository` abstractions for data operations.
  - **Gson**: Processing and deserialization of JSON ledger data.
- **Dependency Injection**: Dagger 2 (`AppComponent`, `RepositoryModule`, `UseCaseModule`, `ExternalModule`).
- **State Machine**: Centralized state transition rules (`StateMachine.kt`) enforcing valid status changes for items (`AVAILABLE`, `ON_LOAN`, `LOST`, `RETIRED`) and checkouts (`ACTIVE`, `RETURNED`, `LOST`).

---

## Build Instructions

You can build and test the project using Gradle command line or directly through Android Studio.

### 1. Build the Project
To compile and assemble the debug APK:
```bash
./gradlew assembleDebug
```
Or on Windows:
```cmd
gradlew.bat assembleDebug
```

### 2. Run Unit Tests
To run all unit tests across domain use cases and data mappers:
```bash
./gradlew testDebugUnitTest
```

### 3. Install and Launch on Device / Emulator
Ensure an Android device or emulator running SDK 35 or higher is connected:
```bash
./gradlew installDebug
```

---

## Project Structure

```
app/src/main/java/com/example/makerhub/
├── common/              # State machine rules, UI utilities, Date helpers
├── data/
│   ├── local/           # Room Database, DAOs, and Entities
│   ├── remote/          # JSON ledger responses and custom adapters
│   └── repository/      # Repository interfaces and implementations
├── di/                  # Dagger components and modules
├── domain/
│   ├── model/           # Domain models and mapper extensions
│   └── usecase/         # Core business use cases
└── presentation/
    └── ui/              # ViewModels, Activities, and RecyclerView Adapters
```
