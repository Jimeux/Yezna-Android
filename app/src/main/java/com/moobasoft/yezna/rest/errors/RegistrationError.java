package com.moobasoft.yezna.rest.errors;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.GsonConverterFactory;

public class RegistrationError extends ErrorBase {

    private List<String> username;
    private List<String> email;
    private List<String> password;

    @SuppressWarnings("unchecked")
    public static final Converter<ResponseBody, RegistrationError> CONVERTER =
            (Converter<ResponseBody, RegistrationError>) GsonConverterFactory
                    .create().responseBodyConverter(RegistrationError.class, null, null);

    public String getUsername() {
        return formatError(username);
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

    public String getEmail() {
        return formatError(email);
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public String getPassword() {
        return formatError(password);
    }

    public void setPassword(List<String> password) {
        this.password = password;
    }
}
