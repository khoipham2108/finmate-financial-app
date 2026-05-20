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

### 4. AI Behavior & Ràng buộc an toàn
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
