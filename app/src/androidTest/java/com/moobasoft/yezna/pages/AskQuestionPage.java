package com.moobasoft.yezna.pages;

import com.moobasoft.yezna.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class AskQuestionPage extends MainPage {

    public AskQuestionPage() {
        onView(withId(R.id.ask_question_page)).check(matches(isDisplayed()));
    }

}
