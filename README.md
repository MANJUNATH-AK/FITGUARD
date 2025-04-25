# FitGuard - A Comprehensive Android Fitness and Health Tracking App

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Usage](#usage)
- [Security Note](#security-note)
- [Contributing](#contributing)
- [Future Scope](#future-scope)
- [Contact](#contact)
- [License](#license)

## Overview
FitGuard is an Android application developed to track fitness and health metrics, created by **Manjunath A K** 

The app provides a user-friendly platform for monitoring physical activities, setting health goals, and promoting a healthy lifestyle, leveraging Android’s capabilities for real-time insights.

## Features
- **Activity Tracking**: Monitor steps, distance, and calories burned.
- **Workout Logging**: Record workouts like running, cycling, and strength training.
- **Health Metrics**: Track heart rate and sleep patterns (device-dependent).
- **Goal Setting**: Set and track personalized fitness goals.
- **User-Friendly Interface**: Intuitive design for seamless navigation.
- **Backend Integration**: Uses [Google Cloud services](https://cloud.google.com/) (e.g., [Firebase](https://firebase.google.com/)) for data storage and synchronization.

*Note*: Refer to the project report for detailed functionality, as features may vary based on implementation.

## Project Structure
The project is documented in a comprehensive report, including:
- **Introduction**: Project background and objectives.
- **Abstract and Synopsis**: High-level overview and scope.
- **Data Flow Diagram (**[DFD](https://en.wikipedia.org/wiki/Data-flow_diagram)**)**: Data flow within the app.
- **E-R Diagram**: Database schema and relationships.
- **System Design**: App architecture details.
- **Implementation**: Codebase and development process.
- **System Testing**: Functionality validation.
- **Snapshots**: App interface screenshots.
- **Conclusion and Future Scope**: Summary and potential enhancements.

## Technologies Used
- **Platform**: [Android](https://www.android.com/)
- **Development Tools**: [Android Studio](https://developer.android.com/studio), Java/Kotlin (assumed)
- **Backend**: [Google Cloud](https://cloud.google.com/) services, likely [Firebase](https://firebase.google.com/)
- **APIs**: Possible integration with [Google Fit](https://developers.google.com/fit)
- **Database**: Local [SQLite](https://www.sqlite.org/) or cloud-based [Firestore](https://firebase.google.com/products/firestore)

## Installation
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/MANJUNATH-AK/FITGUARD.git
   ```
2. **Open in Android Studio**:
   - Launch [Android Studio](https://developer.android.com/studio).
   - Select `Open an existing project` and choose the cloned repository folder.
3. **Configure Dependencies**:
   - Update `build.gradle` with required SDKs and dependencies (e.g., Firebase, Google Fit).
   - Sync the project with Gradle.
4. **Run the App**:
   - Connect an Android device or use an emulator.
   - Build and run via Android Studio.

*Important*: Remove sensitive files (e.g., `app/fitguard-2661e-7771427cfb1e.json`) and use environment variables or a secret manager for credentials.

## Usage
1. **Launch the App**: Open FitGuard on your Android device.
2. **Set Up Profile**: Enter details (e.g., age, weight) for personalized tracking.
3. **Track Activities**: Start logging workouts or daily activities.
4. **View Insights**: Access dashboards for progress monitoring.
5. **Sync Data**: Connect to [Google Fit](https://www.google.com/fit/) or other services for enhanced tracking.

## Security Note
A sensitive [Google Cloud Service Account key](https://cloud.google.com/iam/docs/service-account-creds) (`app/fitguard-2661e-7771427cfb1e.json`) was detected in the repository, triggering a [GitHub push protection error](https://docs.github.com/en/code-security/secret-scanning/working-with-secret-scanning-and-push-protection/working-with-push-protection-from-the-command-line). To resolve:
- Remove the file from history using [git filter-repo](https://github.com/newren/git-filter-repo):
  ```bash
  git filter-repo --path app/fitguard-2661e-7771427cfb1e.json --invert-paths
  ```
- Update `.gitignore`:
  ```bash
  echo "app/fitguard-2661e-7771427cfb1e.json" >> .gitignore
  ```
- Revoke the credential in [Google Cloud Console](https://console.cloud.google.com/) and use secure methods (e.g., [Firebase secrets](https://firebase.google.com/docs/projects/learn-more#secret-manager)) for API keys.

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
- Integration with [Wear OS](https://wearos.google.com/) for wearable device support.
- Social features like challenges or leaderboards.
- Additional health metrics (e.g., blood pressure, glucose levels).
- Expansion to [iOS](https://www.apple.com/ios/) for cross-platform support.

## Contact
For queries, contact Manjunath A K via the repository’s [issues section](https://github.com/MANJUNATH-AK/FITGUARD/issues) or through:

## License
This project is for academic purposes and not distributed under a specific license. All rights reserved by Manjunath A K 
