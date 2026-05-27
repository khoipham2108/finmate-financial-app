# FinMate 

> A clean, minimalist personal finance management application for Android, powered by next-generation AI reasoning to deliver factual, real-time spending insights directly from user transaction history.

---

## Design Philosophy: Minimalism

FinMate is built from the ground up following the **Minimalism Design Style**. The user interface focuses strictly on content scannability, utilizing clean layouts, precise typography, and purposeful spacing to eliminate cognitive overload, ensuring users can track and understand their financial status in seconds.

---

## Tech Stack & Core Infrastructure

The application architecture is strictly decoupled following modern Android development practices to ensure performance, type-safety, and robust security.

* **Client Platform:** Android (Native Kotlin)
* **UI Framework:** Jetpack Compose (Declarative UI Pattern)
* **Asynchronous Execution:** Kotlin Coroutines & Architecture-aware Lifecycles
* **Architecture Pattern:** MVVM (Model-View-ViewModel) with clean separation of concerns
* **Cloud Database:** Google Cloud Firestore (Real-time NoSQL document structure)
* **Authentication Layer:** Firebase Authentication (Secure Email & Password pipeline)
* **AI Engine:** **Gemini 2.5 Flash** (Next-generation high-speed reasoning family)
* **Networking Backend:** Google AI Client SDK v0.9.0 (Utilizing the stable production `v1` endpoint)

---

## FinBot Assistant: Architecture & AI Pipeline

FinBot is not just a generic conversational agent; it acts as an intelligent financial analyst with access to your real-time cloud database.

### 1. Real-time Context Injection
When a user requests financial breakdowns (e.g., *"How much did I spend on gasoline this week?"*), the `ChatViewModel` fetches the user's authentic transaction entries from the `FinanceRepository` (linked to Firestore). This raw data is dynamically injected as localized text context into the Gemini 2.5 Flash prompt, allowing the model to execute precise factual calculations.

### 2. Thread-Safety & Asynchronous Flow
All data fetching and AI inference tasks are securely offloaded from the Main Thread using `Dispatchers.IO`. This architectural boundary guarantees zero UI freezing or dropped frames during heavy payload transfers.

### 3. Smart Window Insets Handling
The text entry layout utilizes Jetpack Compose `Modifier.navigationBarsPadding().imePadding()`. This structural configuration ensures that when the virtual software keyboard slides up, the chat input bar seamlessly adjusts upward, keeping the user's typed text 100% visible and interactive.

### 4. AI Behavior & Safety Constraints
FinBot operates under a strict system instruction matrix:
* **Factual Analytics:** It only performs structural breakdowns based on historical transactions.
* **No Predictive Forecasting:** It is strictly prohibited from delivering financial advice or investment speculations.
* **Dynamic Language Adaptation:** Seamlessly detects and replies in natural English or Vietnamese depending on user input.

---

## Core Package Structure

The repository maintains a strict hierarchical module directory to uphold codebase maintainability:

```text
com.example.finmate/
│
├── data/
│   ├── model/           # Declarative data schemas (e.g., Transaction.kt, User.kt)
│   └── repository/      # Firestore synchronization engine (e.g., FinanceRepository.kt)
│
├── viewmodel/           # Independent state management layer (ChatViewModel.kt, AuthViewModel.kt)
│
└── ui/
    ├── components/      # Reusable atomic UI widgets (e.g., ChatBubble.kt)
    ├── screens/         # Feature-specific viewport architectures
    │   ├── AuthScreen.kt       # Secure Firebase login/register entry
    │   ├── DashboardScreen.kt  # Real-time visual data summaries
    │   ├── ReportScreen.kt     # Deep historical financial analytics
    │   ├── ChatScreen.kt       # FinBot AI assistant chat interface
    │   ├── HistoryScreen.kt    # Chronological transaction logs
    │   ├── ProfileScreen.kt    # User configuration panel
    │   └── MainScreen.kt       # Persistent application wrapper
    │
    └── theme/           # Minimalism Design System configuration (Color.kt, Theme.kt, Type.kt)
---

## Data Visualization & Reporting

FinMate converts abstract financial records into structured actionable insights through a premium suite of minimalist charts implemented in the `DashboardScreen` and `ReportScreen`:

* **Line Charts:** Used to trace cumulative net worth and daily expense trends over time.
* **Pie Charts:** Dedicated to displaying categorical distributions (e.g., Food, Shopping, Transport ratio).
* **Bar Charts:** Optimized for contrasting month-over-month or week-over-week budget thresholds.

---

## Getting Started & Local Installation

Follow these steps to configure the development environment and execute the project locally.

### Prerequisites
* Android Studio Jellyfish / Koala (or newer)
* JDK 17 installed and configured
* Android API Level 34+ Target SDK

### API Key Security Configuration
To protect sensitive credentials, the Gemini API key is completely isolated from the version control system.

1. Open your local root directory and locate the `local.properties` file (or create it if it is missing).
2. Append your personal Google AI Studio API key at the bottom of the file:
   ```properties
   GEMINI_API_KEY="YOUR_ACTUAL_API_KEY_HERE"
3.The project build script will automatically inject this value into the compilation pipeline safely, preventing hardcoded leaks inside `ChatViewModel.kt`.

### Building the Project

1. Clone the repository to your local workstation:
   ```bash
   git clone [https://github.com/your-username/FinMate.git](https://github.com/your-username/FinMate.git)
2. Launch Android Studio and choose Open an Existing Project, then select the cloned directory.
3. Wait for the IDE to finish indexing, then click on the Sync Project with Gradle Files button at the top right.
4. Connect an Android Virtual Device (Emulator) or a physical debug device.
5. Click the green Run triangle icon (or press Shift + F10) to compile and launch FinMate.
