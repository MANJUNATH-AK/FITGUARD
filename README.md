# FitGuard - A Comprehensive Android Fitness and Health Tracking App

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Modules](#modules)
- [Technologies Used](#technologies-used)
- [Software and Hardware Requirements](#software-and-hardware-requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Security Note](#security-note)
- [Contributing](#contributing)
- [Future Scope](#future-scope)
- [References](#references)
- [Contact](#contact)
- [License](#license)

## Overview
FitGuard is an innovative Android application developed by **Manjunath A K** 

FitGuard provides a radiation-free alternative to wearable fitness trackers by leveraging smartphone sensors to track fitness and health metrics. It integrates [machine learning](https://www.tensorflow.org/lite) (TensorFlow Lite), [Firebase](https://firebase.google.com/) for secure data management, and an intuitive interface to deliver real-time health insights, promoting a safer and healthier lifestyle.

## Features
FitGuard offers a comprehensive suite of features:
- **Step Counting**: Accurately tracks steps using the phone’s accelerometer with smoothing algorithms.
- **Activity Recognition**: Uses [TensorFlow Lite](https://www.tensorflow.org/lite) to classify activities (e.g., sitting, walking, climbing stairs).
- **BMI Calculation**: Computes Body Mass Index (BMI) based on user inputs (height, weight, age) and provides tailored health advice.
- **Heart Rate and Blood Oxygen Monitoring**: Measures heart rate and blood oxygen levels using the phone’s camera and flashlight.
- **Data Visualization**: Displays trends (e.g., BMI, step count) via interactive charts using [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart).
- **Secure Data Management**: Stores user data securely in [Firebase Firestore](https://firebase.google.com/docs/firestore).

## Modules
FitGuard’s modular architecture includes:
- **User Authentication and Profile Management**: Secure login/signup via [Firebase Authentication](https://firebase.google.com/docs/auth).
- **Activity Tracking**: Real-time activity prediction using accelerometer data and TensorFlow Lite.
- **Health Metric Calculation**: Computes BMI, heart rate, and blood oxygen levels.
- **Data Visualization**: Renders interactive charts for health metrics.
- **Data Storage and Retrieval**: Manages data with [Firebase Firestore](https://firebase.google.com/docs/firestore).
- **Notification and Alerts**: Sends reminders to encourage fitness goals.

## Technologies Used
- **Platform**: [Android](https://www.android.com/)
- **Development Tools**: [Android Studio](https://developer.android.com/studio), [Kotlin](https://kotlinlang.org/)
- **Machine Learning**: [TensorFlow Lite](https://www.tensorflow.org/lite) for on-device activity recognition
- **Backend**: [Firebase](https://firebase.google.com/) (Authentication, Firestore)
- **Data Visualization**: [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- **APIs**: [Android Sensor API](https://developer.android.com/guide/topics/sensors), Camera API
- **Libraries**:
  - [Firebase UI Auth](https://github.com/firebase/FirebaseUI-Android) for authentication
  - [Google Play Services](https://developers.google.com/android/guides/setup) for Google Sign-In
  - [AndroidX](https://developer.android.com/jetpack/androidx) for UI components

## Software and Hardware Requirements
- **Software**:
  - [Android Studio](https://developer.android.com/studio) (with Kotlin support)
  - [TensorFlow Lite](https://www.tensorflow.org/lite)
  - [Firebase](https://firebase.google.com/) (Authentication, Firestore)
  - [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- **Hardware**:
  - Android smartphone (minimum API level 24)
  - Sensors: Accelerometer, Camera

## Installation
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/MANJUNATH-AK/FITGUARD.git
   ```
2. **Open in Android Studio**:
   - Launch [Android Studio](https://developer.android.com/studio).
   - Select `Open an existing project` and choose the cloned repository folder.
3. **Configure Dependencies**:
   - Update `build.gradle` with required dependencies (e.g., Firebase, TensorFlow Lite).
   - Sync the project with Gradle.
4. **Run the App**:
   - Connect an Android device (API 24+) or use an emulator.
   - Build and run via Android Studio.

*Note*: Ensure sensitive files (e.g., `app/fitguard-2661e-7771427cfb1e.json`) are removed and credentials are managed securely.

## Usage
1. **Launch the App**: Open FitGuard on your Android device.
2. **Sign Up/Login**: Use email/password or Google Sign-In via [Firebase Authentication](https://firebase.google.com/docs/auth).
3. **Set Up Profile**: Enter details (e.g., age, weight, height) for personalized tracking.
4. **Track Activities**: Monitor steps, activities, and health metrics (BMI, heart rate).
5. **View Insights**: Access dashboards with interactive charts for progress tracking.
6. **Receive Notifications**: Get reminders to stay active and meet fitness goals.

## Security Note
A [Google Cloud Service Account key](https://cloud.google.com/iam/docs/service-account-creds) (`app/fitguard-2661e-7771427cfb1e.json`) was detected in the repository, triggering a [GitHub push protection error](https://docs.github.com/en/code-security/secret-scanning/working-with-secret-scanning-and-push-protection/working-with-push-protection-from-the-command-line). To resolve:
- Remove the file from history using [git filter-repo](https://github.com/newren/git-filter-repo):
  ```bash
  git filter-repo --path app/fitguard-2661e-7771427cfb1e.json --invert-paths
  ```
- Update `.gitignore`:
  ```bash
  echo "app/fitguard-2661e-7771427cfb1e.json" >> .gitignore
  ```
- Revoke the credential in [Google Cloud Console](https://console.cloud.google.com/) and use secure methods (e.g., [Firebase secrets](https://firebase.google.com/docs/projects/learn-more#secret-manager)).

## Contributing
This project was developed for academic purposes. Contributions are not actively sought, but feedback is welcome. To contribute:
1. Fork the repository: [MANJUNATH-AK/FITGUARD](https://github.com/MANJUNATH-AK/FITGUARD).
2. Create a branch:
   ```bash
   git checkout -b feature/your-feature
   ```
3. Commit changes:
   ```bash
   git commit -m "Add your feature"
   ```
4. Push to the branch:
   ```bash
   git push origin feature/your-feature
   ```
5. Open a pull request.

## Future Scope
- **Advanced Features**:
  - Enhanced [TensorFlow Lite](https://www.tensorflow.org/lite) models for activity detection (e.g., running, cycling).
  - AI-driven personalized fitness plans and sleep tracking.
  - Diet tracking and calorie intake recommendations.
- **Community Features**:
  - Fitness challenges and leaderboards.
  - Health forums for user interaction.
- **Data Analytics**:
  - Predictive analytics for health trends.
  - Advanced, customizable visualizations.
- **IoT and Wearable Integration**:
  - Support for IoT devices (e.g., smart scales).
  - Optional synchronization with [Wear OS](https://wearos.google.com/).
- **Global Expansion**:
  - Multi-language support and regional health advice.
  - Enhanced offline capabilities.
- **Regulatory Compliance**:
  - Medical certifications for health monitoring.
  - Integration with healthcare providers.


## References
- [Android Development Documentation](https://developer.android.com)
- [TensorFlow Lite](https://www.tensorflow.org/lite)
- [Firebase Documentation](https://firebase.google.com/docs)
- [MPAndroidChart Library](https://github.com/PhilJay/MPAndroidChart)

## Contact
For queries, contact Manjunath A K via the repository’s [issues section](https://github.com/MANJUNATH-AK/FITGUARD/issues) or:


## License
This project is for academic purposes and not distributed under a specific license. All rights reserved by Manjunath A K.
