/**
 * LambdaTest SmartUI – Appium sample for real-device full-page screenshot.
 * Reference only (Appium/WebDriver); not part of the Android app build.
 *
 * When running on LambdaTest, use wdHelper.executeJSWithArgsFetchValue("smartui.takeScreenshot", config)
 * to trigger SmartUI capture with realDeviceScreenshot (set via capabilities / testInstrumentationRunnerArguments).
 */
import java.util.HashMap;
import java.util.Map;

public class SmartUIAppiumSample {

    // Example: WebDriverHelper or your Appium driver wrapper
    // private WebDriverHelper wdHelper;

    public void realDeviceFullPage() {
        // wdHelper.getURL("https://www.lambdatest.com/");
        Map<String, Object> config = new HashMap<>();
        config.put("screenshotName", "RealDeviceFullPage");
        config.put("fullPage", true);   // Mark true once issue gets resolved
        config.put("pageCount", 15);     // Number of pages for Full Page screenshot (min 1, max 20)
        // wdHelper.executeJSWithArgsFetchValue("smartui.takeScreenshot", config);
    }
}
