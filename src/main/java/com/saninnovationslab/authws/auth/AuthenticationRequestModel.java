package com.saninnovationslab.authws.auth;

public class AuthenticationRequestModel {
    private final String username;
    private final String password;

    public AuthenticationRequestModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
