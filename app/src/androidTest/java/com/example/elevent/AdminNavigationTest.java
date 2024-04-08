package com.example.elevent;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;

import static org.hamcrest.CoreMatchers.endsWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminNavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void navigateToAdminHomeFragment() throws InterruptedException {
        //navigate to ProfileFragment
        Thread.sleep(3000);
        onView(withId(R.id.nav_bar_profile)).perform(click());
        Thread.sleep(3000);

        //click on admin navigation textview
        onView(withId(R.id.admin_navigation)).perform(click());
        Thread.sleep(3000); //wait

        //enter the admin key
        onView(withClassName(endsWith("EditText"))).perform(typeText("abcd1234"), ViewActions.closeSoftKeyboard());
        Thread.sleep(3000); // Wait for text to be entered
        //click ok
        onView(withText("OK")).perform(click());
        Thread.sleep(3000); // wait

        onView(withId(R.id.admin_homepage)).check(matches(isDisplayed()));
    }
}

