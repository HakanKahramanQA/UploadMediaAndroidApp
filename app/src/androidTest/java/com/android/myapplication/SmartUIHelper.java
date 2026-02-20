package com.android.myapplication;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper to trigger SmartUI-style screenshots from Espresso tests.
 * Mirrors the Appium pattern: executeJSFetchValue("smartui.takeScreenshot=realDeviceScreenshot") and realDeviceSmartUI().
 * When run on LambdaTest, use their SDK/runner; locally we capture via UiAutomation.
 */
public final class SmartUIHelper {

    private static final String TAG = "SmartUIHelper";
    private static final String SCRIPT_REAL_DEVICE = "smartui.takeScreenshot=realDeviceScreenshot";

    /**
     * Espresso equivalent of Appium executeJSFetchValue(script).
     * When script is "smartui.takeScreenshot=realDeviceScreenshot", takes a real-device screenshot.
     * Returns a non-null string (e.g. "ok" or file path) for compatibility with Appium usage.
     */
    public static String executeJSFetchValue(String script) {
        if (script != null && script.contains(SCRIPT_REAL_DEVICE)) {
            realDeviceSmartUI();
            return "ok";
        }
        return "";
    }

    /**
     * Same as Appium: realDeviceSmartUI() triggers smartui.takeScreenshot=realDeviceScreenshot.
     * Call this from Espresso tests to get SS (same as wdHelper.executeJSFetchValue("smartui.takeScreenshot=realDeviceScreenshot")).
     */
    public static void realDeviceSmartUI() {
        takeScreenshotWithName("RealDeviceScreenshot");
    }

    /**
     * Real-device full-page style screenshot with config (screenshotName, fullPage, pageCount).
     */
    public static void realDeviceFullPage() {
        Map<String, Object> config = new HashMap<>();
        config.put("screenshotName", "RealDeviceFullPage");
        config.put("fullPage", true);
        config.put("pageCount", 15);  // min 1, max 20 for full-page
        takeScreenshot(config);
    }

    /**
     * Take a SmartUI-style screenshot with the given config.
     * Config keys: screenshotName (String), fullPage (boolean), pageCount (int).
     */
    public static void takeScreenshot(Map<String, Object> config) {
        String name = config != null && config.containsKey("screenshotName")
                ? String.valueOf(config.get("screenshotName"))
                : "SmartUI_" + System.currentTimeMillis();
        takeScreenshotWithName(name);
    }

    private static void takeScreenshotWithName(String name) {
        try {
            Bitmap screenshot = InstrumentationRegistry.getInstrumentation()
                    .getUiAutomation()
                    .takeScreenshot();
            if (screenshot != null) {
                File dir = new File(Environment.getExternalStorageDirectory(), "SmartUI");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, name + ".png");
                try (FileOutputStream out = new FileOutputStream(file)) {
                    screenshot.compress(Bitmap.CompressFormat.PNG, 100, out);
                }
                screenshot.recycle();
                Log.i(TAG, "Screenshot saved: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            Log.e(TAG, "takeScreenshot failed", e);
        }
    }
}
