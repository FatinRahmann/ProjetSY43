# ProjetSY43 - Event Booking App

ProjetSY43 is a mobile application built using **Jetpack Compose** that allows users to register/login, browse events, make fake payments, and manage tickets. The app uses **Firebase** for authentication and real-time data storage, and follows modern **MVVM architecture**.

## ✨ Features

- 🔐 **Authentication**:
  - Firebase Email/Password login
  - Role-based access: Buyer or Seller

- 🎫 **Event Management**:
  - Sellers can add events with name, location, description, image
  - Buyers can browse and book available events

- 💳 **Fake Payment UI**:
  - Simulated credit card form (card number, expiry, CVC)
  - Animated feedback using custom toasts

- 📱 **Ticket System**:
  - Auto-generation of tickets after payment
  - QR code view of ticket details

- 🧪 **UI**:
  - Built fully with **Jetpack Compose**
  - Responsive layouts and form validation

## 🛠️ Tech Stack

- **Jetpack Compose** for UI
- **Firebase Authentication** and Realtime Database
- **MVVM Architecture** with ViewModel
- **Kotlin Coroutines** for async operations
- **Navigation Component** for screen routing

## 📁 Project Structure
    
    ├── app/
    │   ├── factory/
    │   ├── navigation/
    │   ├── model/             # Data classes and session storage
    │   │   ├── entities/
    │   │   └── repository/    # Repository for Firebase read/write
    │   ├── viewModel/         # ViewModels for state and logic
    │   └── ui.theme/
    │        ├── components/   # Reusable UI components (Toast, Buttons, etc.)
    │        └── screens/      # All composable screen UIs
    │   
    
## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/FatinRahmann/ProjetSY43.git
cd ProjetSY43
