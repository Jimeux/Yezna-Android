package com.moobasoft.yezna.events.ask;

import com.moobasoft.yezna.events.Event;
import com.moobasoft.yezna.rest.models.Question;

public class QuestionCreatedEvent implements Event {

    private final Question question;

    public QuestionCreatedEvent(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }
}
