package com.moobasoft.yezna.rest.errors;

import java.util.List;

public class ErrorBase {

    public static String formatError(List<String> errors) {
        if (errors == null || errors.isEmpty())
            return null;

        String error = errors.get(0);

        if (error == null || error.length() < 2)
            return null;

        error = error.trim();

        String firstLetter = error.substring(0, 1).toUpperCase();
        return firstLetter + error.substring(1);
    }

}