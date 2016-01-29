package com.moobasoft.yezna.events.auth;

import com.moobasoft.yezna.events.Event;

public class LoginPromptEvent implements Event {
    private String message;

    public LoginPromptEvent() {
    }

    public LoginPromptEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}