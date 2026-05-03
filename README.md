# Kt AI

AI Coding Agent app for Android/Kotlin developers. Not a regular chatbot — Kt AI works inside your selected project folder to create, edit, fix, and refactor code with AI, all from your phone.

## Tech Stack

- **Kotlin** + **Jetpack Compose** + **Material 3**
- **MVVM** + **Clean Architecture**
- **Hilt** (DI) · **Room** (local DB) · **Ktor** (networking)
- **Supabase** (auth, DB, storage — placeholder integration)
- **Storage Access Framework** (safe, scoped file access)

## Features

- Auth (login, signup, forgot password)
- Home Dashboard with usage tracking
- Workspace system (SAF folder picker)
- File Explorer with multi-select for AI context
- AI Chat (ChatGPT-style) with code block rendering
- Code Diff Preview with apply/cancel/regenerate
- Change History with rollback
- Subscription plans (Free / Pro / Max)
- Usage limit tracking
- Settings, Profile, Security screens
- AI Provider abstraction (DeepSeek, HuggingFace, NVIDIA, OpenRouter, Custom)

## Project Structure

```
app/src/main/java/com/monem/ktai/
├── data/           # Data layer (Room, API, repos)
├── domain/         # Domain models, repository interfaces
├── presentation/   # UI (Compose screens, ViewModels)
│   ├── auth/       # Login, Signup, Forgot Password
│   ├── chat/       # AI Chat
│   ├── common/     # Theme, shared components
│   ├── dashboard/  # Home screen
│   ├── diff/       # Code diff preview
│   ├── explorer/   # File explorer & viewer
│   ├── history/    # Change history
│   ├── navigation/ # NavGraph
│   ├── onboarding/ # Splash, Onboarding
│   ├── profile/    # Profile, Settings, Security
│   ├── subscription/ # Plans, Usage limits
│   └── workspace/  # Workspace management
└── di/             # Hilt modules
```

## Build

```bash
./gradlew assembleDebug
```

## License

MIT
