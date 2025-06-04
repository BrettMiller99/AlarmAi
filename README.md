# Smart Alarm Clock

An intelligent Android application that dynamically adjusts your wake-up time based on real-time traffic conditions to your office.

## Features
- Set your work destination and preferred arrival time
- Automatically adjusts alarm time based on current traffic conditions
- Multiple alarm support with custom settings
- Traffic-aware notifications about potential delays
- Integration with Google Maps API for accurate traffic data

## Prerequisites
- Android Studio (latest stable version)
- Android SDK 24 (Android 7.0) or higher
- Google Maps API key
- Java Development Kit (JDK) 11 or higher

## Getting Started
1. Clone the repository
2. Open the project in Android Studio
3. Add your Google Maps API key to `local.properties`:
   ```
   MAPS_API_KEY=your_api_key_here
   ```
4. Build and run the project on an emulator or physical device

## Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/smartalarm/
│   │   │   ├── data/         # Data layer (repositories, data sources)
│   │   │   ├── di/           # Dependency injection
│   │   │   ├── domain/       # Business logic
│   │   │   ├── ui/           # UI components
│   │   │   │   ├── alarm/    # Alarm related screens
│   │   │   │   ├── settings/ # Settings screens
│   │   │   │   └── ...
│   │   │   └── utils/        # Utility classes
│   │   └── res/             # Resources
│   └── test/                # Unit tests
└── build.gradle             # Project level build file
```

## Tech Stack
- Kotlin
- Android Jetpack Components
  - Room (local database)
  - ViewModel & LiveData
  - WorkManager (for scheduling alarms)
  - Navigation Component
- Retrofit (for API calls)
- Google Maps SDK
- Hilt (Dependency Injection)
- Coroutines & Flow

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
