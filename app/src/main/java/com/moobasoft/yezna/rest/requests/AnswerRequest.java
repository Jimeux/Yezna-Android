package com.moobasoft.yezna.rest.requests;

public class AnswerRequest {

    private Answer answer;

    public AnswerRequest(boolean isYes) {
        this.answer = new Answer(isYes);
    }

    class Answer {
        private boolean isYes;

        public Answer(boolean isYes) {
            this.isYes = isYes;
        }

        public boolean isYes() {
            return isYes;
        }

        public void setIsYes(boolean isYes) {
            this.isYes = isYes;
        }
    }
}