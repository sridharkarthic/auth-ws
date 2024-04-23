package com.saninnovationslab.authws.context;

public class AuthError {

    private final String message;
    private final Integer status;

    public AuthError(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public Integer getStatus() {
        return status;
    }

}
