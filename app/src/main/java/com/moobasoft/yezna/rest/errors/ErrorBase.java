package com.moobasoft.yezna.rest.errors;

import java.util.List;

public class ErrorBase {

    public static String composeErrors(List<String> errors) {
        if (errors == null || errors.isEmpty()) return null;
        String errorString = "";
        for (String s : errors)
            errorString += s + "\n";
        return errorString.trim();
    }

}