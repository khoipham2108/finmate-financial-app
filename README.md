# FinMate

> A clean, minimalist personal finance management application for Android, powered by next-generation AI reasoning to deliver factual, real-time spending insights directly from user transaction history.

---

# Design Philosophy: Minimalism

FinMate is built from the ground up following the **Minimalism Design Style**. The user interface focuses strictly on content scannability, utilizing clean layouts, precise typography, and purposeful spacing to eliminate cognitive overload. This allows users to track and understand their financial status within seconds.

---

# Tech Stack & Core Infrastructure

The application architecture follows modern Android development practices to ensure performance, scalability, type-safety, and security.

* **Client Platform:** Android (Native Kotlin)
* **UI Framework:** Jetpack Compose (Declarative UI)
* **Asynchronous Execution:** Kotlin Coroutines & Lifecycle-aware APIs
* **Architecture Pattern:** MVVM (Model–View–ViewModel)
* **Cloud Database:** Google Cloud Firestore
* **Authentication Layer:** Firebase Authentication
* **AI Engine:** Gemini 2.5 Flash
* **Networking Backend:** Google AI Client SDK v0.9.0 (`v1` production endpoint)

---

# FinBot Assistant: AI Architecture & Processing Pipeline

FinBot is not just a generic chatbot. It functions as an intelligent financial assistant capable of analyzing real-time user transaction data directly from the cloud database.

## 1. Real-time Context Injection

When a user sends a request such as:

> “How much did I spend on gasoline this week?”

The `ChatViewModel` dynamically retrieves transaction records from the `FinanceRepository`, which synchronizes with Firestore. These records are injected into the Gemini prompt as structured contextual data, enabling the AI model to perform accurate factual calculations.

## 2. Thread-Safety & Asynchronous Processing

All database operations and AI inference tasks are executed using `Dispatchers.IO`, ensuring heavy operations never block the Main Thread. This guarantees smooth UI performance without frame drops or freezing.

## 3. Smart Window Insets Handling

The chat input area is implemented using:

```kotlin
Modifier
    .navigationBarsPadding()
    .imePadding()
```

This ensures the message input field automatically adjusts when the software keyboard appears, keeping user interactions fully visible and accessible.

## 4. AI Safety & Behavioral Constraints

FinBot follows a strict system instruction policy:

* Performs only factual transaction-based analytics
* Does not provide investment or financial advice
* Avoids predictive forecasting or speculative recommendations
* Automatically adapts between Vietnamese and English based on user input

---

# Project Structure

The repository follows a clean hierarchical architecture for maintainability and scalability.

```text
com.example.finmate/
│
├── data/
│   ├── model/               # Data schemas (Transaction.kt, User.kt)
│   └── repository/          # Firestore synchronization layer
│
├── viewmodel/               # State management layer
│   ├── AuthViewModel.kt
│   └── ChatViewModel.kt
│
└── ui/
    ├── components/          # Reusable UI components
    ├── screens/
    │   ├── AuthScreen.kt
    │   ├── DashboardScreen.kt
    │   ├── ReportScreen.kt
    │   ├── ChatScreen.kt
    │   ├── HistoryScreen.kt
    │   ├── ProfileScreen.kt
    │   └── MainScreen.kt
    │
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

# Data Visualization & Reporting

FinMate transforms financial records into actionable insights through a suite of minimalist visual analytics integrated into the `DashboardScreen` and `ReportScreen`.

## Supported Visualizations

### Line Charts

Used to visualize:

* Daily spending trends
* Net worth progression over time
* Expense fluctuations

### Pie Charts

Used to display category distributions such as:

* Food
* Shopping
* Transportation
* Entertainment

### Bar Charts

Used for comparative analytics including:

* Monthly spending comparison
* Weekly budget tracking
* Expense category contrasts

---

# Getting Started

Follow these steps to set up and run the project locally.

## Prerequisites

Make sure the following tools are installed:

* Android Studio Jellyfish / Koala or newer
* JDK 17
* Android SDK API Level 34+

---

# API Key Configuration

To protect sensitive credentials, the Gemini API key is excluded from version control.

## Step 1 — Open `local.properties`

Locate the `local.properties` file in the root directory.

If it does not exist, create one.

## Step 2 — Add Your API Key

Append the following line:

```properties
GEMINI_API_KEY="YOUR_ACTUAL_API_KEY_HERE"
```

## Step 3 — Automatic Injection

The Gradle build system automatically injects this key during compilation, preventing hardcoded credentials inside the source code.

---

# Building & Running the Project

## 1. Clone the Repository

```bash
git clone https://github.com/your-username/FinMate.git
```

## 2. Open in Android Studio

Launch Android Studio and select:

> Open an Existing Project

Then choose the cloned repository folder.

## 3. Sync Gradle

Wait for indexing to complete, then click:

> Sync Project with Gradle Files

## 4. Connect a Device

Use either:

* Android Emulator (AVD)
* Physical Android device with USB debugging enabled

## 5. Run the Application

Click the green **Run** button or press:

```text
Shift + F10
```

FinMate will compile and launch automatically.

---

# Key Features

* Minimalist modern UI
* Real-time Firestore synchronization
* AI-powered spending analysis
* Firebase Authentication integration
* Smart multilingual AI assistant
* Secure API key management
* Responsive Jetpack Compose layouts
* Real-time financial reporting

---

# Future Improvements

Potential future enhancements include:

* Budget goal tracking
* Receipt scanning with OCR
* AI-generated financial summaries
* Export reports to PDF/Excel
* Multi-device synchronization optimization
* Offline caching support
* Dark mode customization

---

# License

This project is intended for educational and portfolio purposes.
