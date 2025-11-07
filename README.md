**Beta Budget (Todo App)
A feature-rich, offline-first Android application built in Kotlin. This app demonstrates a modern Android architecture using a public REST API, a local Room database for offline support, and Firebase for blob storage.

📋 Overview
This app began as a budget tracker concept and evolved into a robust "Todo" list application. It is designed to be fully functional, secure, and user-friendly, showcasing a wide range of modern Android development practices.

The core of the app is its "offline-first" architecture. Users can create new todo items instantly, even with no internet connection. These items are saved to a local RoomDB and then automatically synchronized with a live REST API when the connection is restored.

✨ Features
Authentication: Full Login & Registration flow using a live REST API (dummyjson.com).

Offline-First Sync:

New todos are saved to a local RoomDB instantly.

The app automatically syncs new items to the remote server when the app is opened with an internet connection.

The UI provides a visual cue for items that are "Pending sync...".

NoSQL & Blob Storage:

Users can upload a profile picture using the device's photo picker.

The image file is uploaded to Firebase Storage (Blob Storage).

The public URL is then saved in Cloud Firestore (NoSQL DB).

The profile picture is loaded from the URL in the settings screen.

In-App Settings:

Language Toggle: Users can switch the app's language in real-time between English, Afrikaans, isiZulu, isiXhosa, or follow the system default.

Notification Toggle: Users can enable or disable local notifications.

Logout: Securely clears the user's session token and local database.

Real-time Notifications:

Sends a local push notification when a new todo is successfully saved.

Handles runtime notification permissions for Android 13+.

Modern UI & Navigation:

Uses a BottomNavigationView to host the main TodoListFragment and SettingsFragment.

Supports Light and Dark themes based on the user's system settings.

Uses RecyclerView to display lists efficiently.

🛠️ Technologies Used
Core: Kotlin

Architecture: Repository Pattern, Offline-First

Asynchronous: Kotlin Coroutines

Networking (REST API):

Retrofit: For type-safe HTTP requests.

OkHttp: For logging network traffic.

Local Database (Offline Cache):

RoomDB: As the local "single source of truth" for todos.

Cloud (NoSQL & Blob Storage):

Firebase Storage: For storing profile picture blobs.

Cloud Firestore: For storing the profile picture URL.

Data Management:

SharedPreferences (via SessionManager): For user tokens and settings.

UI & Navigation:

Fragments & BottomNavigationView

RecyclerView & MaterialCardView

SwitchMaterial

XML Layouts & Themes (Light/Dark)

Image Loading:

Glide: For loading the profile picture from a URL.**
