#!/usr/bin/env bash
# Build the app and the Espresso test APK (instrumented test suite).
# Usage: ./build-espresso-test-app.sh

set -e
cd "$(dirname "$0")"

echo "Building app (debug)..."
./gradlew assembleDebug --no-daemon

echo "Building Espresso test APK..."
./gradlew assembleDebugAndroidTest --no-daemon

echo "Done."
echo "  App:        app/build/outputs/apk/debug/app-debug.apk"
echo "  Test APK:   app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
