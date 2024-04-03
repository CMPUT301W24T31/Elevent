package com.example.elevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.elevent.Admin.AdminHomeFragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminHomeFragmentNavigationTest {

    @Rule
    public ActivityScenarioRule<AdminHomeFragment> activityRule = new ActivityScenarioRule<>(AdminHomeFragment.class);

    @Test
    public void testNavigateToAdminEventFragment() {
        // Click on the "events" button
        onView(withId(R.id.admin_events_button)).perform(click());

        // Check if AdminEventFragment is displayed
        onView(withId(R.id.admin_fragment_container)).check(matches(isDisplayed()));
        // Additional checks can be performed to verify if specific elements from AdminEventFragment are displayed
    }

    @Test
    public void testNavigateToAdminImageFragment() {
        // Click on the "images" button
        onView(withId(R.id.admin_images_button)).perform(click());

        // Check if AdminImageFragment is displayed
        onView(withId(R.id.admin_fragment_container)).check(matches(isDisplayed()));
        // Additional checks can be performed to verify if specific elements from AdminImageFragment are displayed
    }
}