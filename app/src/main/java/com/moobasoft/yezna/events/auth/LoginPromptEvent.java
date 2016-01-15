package com.moobasoft.yezna.events.auth;

public class LoginPromptEvent {
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