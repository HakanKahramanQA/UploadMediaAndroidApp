#!/usr/bin/env bash
# Build app + test APK, install on connected device/emulator, and run Espresso tests.
# Usage: ./run-espresso-tests.sh
# Requires: device or emulator connected (adb devices)

set -e
cd "$(dirname "$0")"

echo "Running Espresso test suite on connected device..."
./gradlew connectedDebugAndroidTest --no-daemon

echo "Done. Reports: app/build/reports/androidTests/connected/"
