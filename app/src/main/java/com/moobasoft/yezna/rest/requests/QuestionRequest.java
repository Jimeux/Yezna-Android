package com.moobasoft.yezna.rest.requests;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class QuestionRequest {
        private RequestBody question;
        private RequestBody isPublic;
        private RequestBody timeLimit;
        private RequestBody image;

    public QuestionRequest(String question, boolean isPublic, int timeLimit, String imagePath) {
        this.question = RequestBody.create(MediaType.parse("text/plain"), question);
        this.isPublic = RequestBody.create(MediaType.parse("text/plain"), Boolean.toString(isPublic));
        this.timeLimit = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(timeLimit));
        if (imagePath != null) {
            File file = new File(imagePath);
            this.image = RequestBody.create(MediaType.parse("image/*"), file);
        }
    }
}