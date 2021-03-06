package com.moobasoft.yezna.pages;

import com.moobasoft.yezna.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class MyQuestionsPage extends MainPage {

    public MyQuestionsPage() {
        onView(withId(R.id.my_questions_page)).check(matches(isDisplayed()));
    }

}
