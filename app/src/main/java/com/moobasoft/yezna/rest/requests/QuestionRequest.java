package com.moobasoft.yezna.rest.requests;

import com.moobasoft.yezna.rest.models.Question;

public class QuestionRequest {
    private Question question;

    public QuestionRequest(String question, boolean isPublic, int timeLimit) {
        this.question = new Question();
        this.question.setQuestion(question);
        this.question.setIsPublic(isPublic);
        this.question.setTimeLimit(timeLimit);
    }
}