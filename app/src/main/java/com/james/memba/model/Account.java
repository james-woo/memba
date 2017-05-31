package com.james.memba.model;

public class Account {
    private String mEmail;
    private String mToken;
    private String mUserId;

    public Account(String email, String token, String userId) {
        mEmail = email;
        mToken = token;
        mUserId = userId;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getToken() {
        return mToken;
    }

    public String getUserId() {
        return mUserId;
    }
}
