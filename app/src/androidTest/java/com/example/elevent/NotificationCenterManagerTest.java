package com.example.elevent;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NotificationCenterManagerTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void notificationCenterManagerTest() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.nav_bar_myevents), withContentDescription("My Events"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_main_navigation_bar),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        DataInteraction linearLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.my_events_list),
                        childAtPosition(
                                withId(R.id.my_events_frame_layout),
                                0)))
                .atPosition(0);
        linearLayout.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.manage_the_event), withText("manage"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                9)));
        materialButton.perform(scrollTo(), click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.notif_centre_button), withText("notification centre"),
                        childAtPosition(
                                allOf(withId(R.id.container_relative_layout),
                                        childAtPosition(
                                                withId(R.id.activity_main_framelayout),
                                                0)),
                                0),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.add_notif_button), withText("post new notification"),
                        withParent(withParent(withId(R.id.activity_main_framelayout))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.add_notif_button), withText("post new notification"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.activity_main_framelayout),
                                        0),
                                1),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction button2 = onView(
                allOf(withId(android.R.id.button1), withText("Post"),
                        withParent(withParent(withId(androidx.appcompat.R.id.buttonPanel))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.notification_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.appcompat.R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("notiTest"), closeSoftKeyboard());

        ViewInteraction editText = onView(
                allOf(withId(R.id.notification_text), withText("notiTest"),
                        withParent(withParent(withId(androidx.appcompat.R.id.custom))),
                        isDisplayed()));
        editText.check(matches(withText("notiTest")));

        ViewInteraction materialButton4 = onView(
                allOf(withId(android.R.id.button1), withText("Post"),
                        childAtPosition(
                                childAtPosition(
                                        withId(androidx.appcompat.R.id.buttonPanel),
                                        0),
                                3)));
        materialButton4.perform(scrollTo(), click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
