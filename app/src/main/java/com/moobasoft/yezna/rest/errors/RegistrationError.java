package com.moobasoft.yezna.rest.errors;

import com.squareup.okhttp.ResponseBody;

import java.util.List;

import retrofit.Converter;
import retrofit.GsonConverterFactory;

public class RegistrationError extends ErrorBase {

    private List<String> username;
    private List<String> email;
    private List<String> password;

    @SuppressWarnings("unchecked")
    public static final Converter<ResponseBody, RegistrationError> CONVERTER =
            (Converter<ResponseBody, RegistrationError>) GsonConverterFactory
                    .create().fromResponseBody(RegistrationError.class, null);

    public String getUsername() {
        return composeErrors(username);
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

    public String getEmail() {
        return composeErrors(email);
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public String getPassword() {
        return composeErrors(password);
    }

    public void setPassword(List<String> password) {
        this.password = password;
    }
}
