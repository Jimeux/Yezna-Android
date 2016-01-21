package com.moobasoft.yezna.pages;

import com.moobasoft.yezna.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

public class PublicQuestionsPage extends MainPage {

    public PublicQuestionsPage() {
        onView(withId(R.id.public_questions_page)).check(matches(isDisplayed()));
    }

    public void clickYesButton() {
        onView(allOf(withId(R.id.yes_btn), isEnabled())).perform(click());
    }

    public void clickNoButton() {
        onView(allOf(withId(R.id.no_btn), isEnabled())).perform(click());
    }

}
