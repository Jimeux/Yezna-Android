package com.moobasoft.yezna.pages;

import android.support.test.espresso.contrib.DrawerActions;

import com.moobasoft.yezna.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class MainPage {
    public MainPage() {
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
    }

    public MainPage openDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        return this;
    }

    public MainPage closeDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        return this;
    }

    public MainPage logOut() {
        openDrawer();
        onView(withText(R.string.logout)).perform(click());
        return this;
    }

    public LoginPage navigateToLoginPage() {
        openDrawer();
        onView(withText(R.string.login)).perform(click());
        return new LoginPage();
    }

    public LoginPage navigateToRegistrationPage() {
        openDrawer();
        onView(withText(R.string.register)).perform(click());
        return new LoginPage();
    }

    public PublicQuestionsPage navigateToPublicQuestionsPage() {
        openDrawer();
        onView(withText(R.string.public_questions)).perform(click());
        return new PublicQuestionsPage();
    }

    public AskQuestionPage navigateToAskQuestionPage() {
        openDrawer();
        onView(withText(R.string.ask_question)).perform(click());
        return new AskQuestionPage();
    }

    public MyQuestionsPage navigateToMyQuestionsPage() {
        openDrawer();
        onView(withText(R.string.my_questions)).perform(click());
        return new MyQuestionsPage();
    }

    public LoginPage navigateToProfilePage() {
        openDrawer();
        onView(withText(R.string.profile)).perform(click());
        return new LoginPage();
    }

}
