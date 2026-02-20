package com.android.myapplication;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class FileUploadEspressoTest {

    private static final int WAIT_FOR_LIST_POLL_MS = 500;
    private static final int WAIT_FOR_LIST_MAX_ATTEMPTS = 25;  // up to ~12.5s for list to load

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private UiDevice uiDevice;

    @Before
    public void setUp() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        handleAllFilesAccessScreenIfShown();
    }

    /**
     * If the "All files access" settings page is shown: click Allow, enable the toggle, then return to app.
     * Only presses Back if we actually interacted with Settings (so we don't exit the app when already on main screen).
     */
    private void handleAllFilesAccessScreenIfShown() {
        uiDevice.waitForIdle(1000);

        // Wait for All files access / Settings screen to open (app may have launched it)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }

        boolean wasOnSettingsScreen = false;

        // 1. Click "Allow" if it's a button or clickable text (e.g. in a popup or list)
        try {
            UiObject allowButton = uiDevice.findObject(new UiSelector().text("Allow"));
            if (allowButton.exists()) {
                allowButton.click();
                uiDevice.waitForIdle(800);
                wasOnSettingsScreen = true;
            }
        } catch (Exception ignored) {
        }
        if (!wasOnSettingsScreen) {
            try {
                UiObject allowText = uiDevice.findObject(new UiSelector().textContains("Allow"));
                if (allowText.exists()) {
                    allowText.click();
                    uiDevice.waitForIdle(800);
                    wasOnSettingsScreen = true;
                }
            } catch (Exception ignored) {
            }
        }

        // 2. Enable the "Allow" permission toggle (Switch) if present and not already on
        try {
            UiObject switchWidget = uiDevice.findObject(new UiSelector().className("android.widget.Switch"));
            if (switchWidget.exists()) {
                if (!switchWidget.isChecked()) {
                    switchWidget.click();
                    uiDevice.waitForIdle(500);
                }
                wasOnSettingsScreen = true;
            }
        } catch (Exception ignored) {
        }

        // 3. Return to app main page only if we were on the Settings screen (avoid exiting app)
        if (wasOnSettingsScreen) {
            uiDevice.pressBack();
            uiDevice.waitForIdle(1500);
        }
    }

    /**
     * 1 - FileUpload: Launch app, wait for content to load, verify at least one file appears.
     */
    @Test
    public void fileUpload_launchApp_verifyFileAppearsOnPage() throws InterruptedException {
        // Wait for media list to load (poll until RecyclerView has at least one item or timeout)
        waitForRecyclerViewToHaveItems(1);

        // Verify at least one file (list item) is shown in the RecyclerView
        Espresso.onView(withId(R.id.recycler))
                .check(recyclerViewHasMinimumItemCount(1));
        // SmartUI SS (same as Appium)
        SmartUIHelper.executeJSFetchValue("smartui.takeScreenshot=realDeviceScreenshot");
    }

    /**
     * Wait until the RecyclerView has at least minItems (polling on main thread).
     */
    private void waitForRecyclerViewToHaveItems(final int minItems) throws InterruptedException {
        for (int i = 0; i < WAIT_FOR_LIST_MAX_ATTEMPTS; i++) {
            Thread.sleep(WAIT_FOR_LIST_POLL_MS);
            final int[] count = { -1 };
            activityRule.getScenario().onActivity(activity -> {
                RecyclerView rv = activity.findViewById(R.id.recycler);
                count[0] = (rv != null && rv.getAdapter() != null) ? rv.getAdapter().getItemCount() : 0;
            });
            if (count[0] >= minItems) {
                return;
            }
        }
        fail("RecyclerView did not show at least " + minItems + " item(s) within timeout");
    }

    /**
     * 2 - AppLaunch: Launch app and verify main screen (RecyclerView) is displayed.
     */
    @Test
    public void appLaunch_mainScreenIsDisplayed() {
        Espresso.onView(withId(R.id.recycler))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        // SmartUI SS (same as Appium)
        SmartUIHelper.executeJSFetchValue("smartui.takeScreenshot=realDeviceScreenshot");
    }

    private static ViewAssertion recyclerViewHasMinimumItemCount(int minCount) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }
                assertTrue(view instanceof RecyclerView);
                RecyclerView rv = (RecyclerView) view;
                assertTrue("RecyclerView should have at least " + minCount + " item(s), got " + getAdapterItemCount(rv),
                        getAdapterItemCount(rv) >= minCount);
            }
        };
    }

    private static int getAdapterItemCount(RecyclerView rv) {
        return (rv.getAdapter() != null) ? rv.getAdapter().getItemCount() : 0;
    }
}
