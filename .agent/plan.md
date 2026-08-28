# Project Plan

KIIT GREENROUTES: A comprehensive bus tracking and transport management app for KIIT University.

Main Features:
- Live Bus Tracking (GPS)
- Route & Stops (Bus 56, 35, 66, 30, 38)
- ETA Prediction
- SOS / Emergency Button
- Notifications & Alerts (Proximity, Delay)
- Pass / Ticket System (QR Code)
- Settings (Profile, Map Styles, Themes, Languages)

Routes to implement:
1. Day Scholar (Ravi Talkies -> Campus 25 via Routes 56, 35)
2. Day Scholar (Laxmisagar -> Campus 25 via Route 66)
3. Internal Shuttle (Campus 15 -> Campus 25 via Route 30)
4. Internal Shuttle (Campus 6 -> Campus 25 via Route 38)

UI Requirements:
- Material 3 Design
- Vibrant Green Motif
- Adaptive Layouts
- Edge-to-Edge display

## Project Brief

# Project Brief: KIIT GREENROUTES

KIIT GREENROUTES is a modern, Material 3-based transit application designed to streamline commuting for students and staff. The app provides real-time visibility into the university's bus network, ensuring efficient travel between campuses and residential areas.

## Features

*   **Live Bus Tracking & ETA**: Real-time GPS monitoring of campus buses (e.g., Routes 56, 35, 66) with predictive arrival times for upcoming stops.
*   **Interactive Route Map**: A vibrant, Material 3 map interface displaying all Day Scholar and Hostel/Internal routes with detailed stop information.
*   **Digital Transport Pass**: A secure QR-code-based digital ID and ticketing system to replace physical bus passes.
*   **Emergency SOS System**: A prominent, high-priority emergency button for instant alerts to university security and support services.
*   **Settings & Preferences**: Comprehensive profile management, notification controls (proximity alerts), and app customization (map styles, dark/light mode).

## High-Level Technical Stack

*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material 3)
*   **Navigation**: Jetpack Navigation 3 (State-driven architecture)
*   **Adaptive Layout**: Compose Material Adaptive Library (supporting 2-pane layouts for large screens/foldables)
*   **Asynchronous Logic**: Kotlinx Coroutines
*   **Location Services**: Google Play Services Location API for real-time tracking
*   **Networking**: Retrofit & OkHttp (for fetching real-time GPS data)
*   **Image Loading**: Coil (for profile and ticket rendering)

## Implementation Steps

### Task_1_Foundation: Establish the project foundation including data models, local persistence, networking, and the Material 3 visual identity.
- **Status:** COMPLETED
- **Updates:** - Defined Bus, Route, and Stop data models.
- Implemented Room database for local route storage with DAO and cross-reference support.
- Configured Retrofit service for bus tracking and GPS updates.
- Set up Material 3 Vibrant Green theme with Light/Dark mode and Edge-to-Edge support.
- Implemented Navigation 3 shell with Adaptive Scaffold and List-Detail strategy.
- Created adaptive app icon.
- Successfully built the project.
- **Acceptance Criteria:**
  - Bus, Route, and Stop data models defined
  - Room database for local route storage implemented
  - Retrofit service for bus tracking defined
  - Material 3 Vibrant Green theme and Edge-to-Edge display configured
  - Navigation 3 shell and adaptive scaffold setup
  - Project builds successfully
- **Duration:** N/A

### Task_2_Tracking_SOS: Implement the core transit features: live bus tracking, interactive maps for all routes, and the emergency SOS system.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Google Play Services Location integrated
  - Interactive Map displaying Routes 56, 35, 66, 30, and 38
  - Live bus location simulation/tracking working
  - High-priority SOS button implemented and functional
  - UI matches Material 3 design guidelines
- **StartTime:** 2026-08-27 20:35:48 IST

### Task_3_User_Features: Develop the Digital Transport Pass system and the Settings/Profile management screens.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Digital Pass with QR code generation implemented
  - Settings screen with Profile, Theme, and Notification options functional
  - Coil integrated for image loading
  - Proximity and delay alerts logic implemented

### Task_4_Final_Verification: Perform a comprehensive run and verify session to ensure app stability and requirement alignment.
- **Status:** PENDING
- **Acceptance Criteria:**
  - App runs without crashes on target SDK 36
  - All Day Scholar and Internal Shuttle routes verified
  - Final build passes successfully
  - Critically verify UI alignment with 'Vibrant, energetic' Material 3 aesthetic

