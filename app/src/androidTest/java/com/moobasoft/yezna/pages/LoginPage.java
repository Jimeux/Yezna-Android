package com.moobasoft.yezna.pages;

import com.moobasoft.yezna.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class LoginPage {

    public LoginPage() {
        onView(withId(R.id.login_page)).check(matches(isDisplayed()));
    }

    public MainPage login() {
        onView(withId(R.id.username_et)).perform(typeText("jim"));
        onView(withId(R.id.password_et)).perform(typeText("passer"));
        onView(withId(R.id.btn_primary)).perform(click());
        return new MainPage();
    }

}
