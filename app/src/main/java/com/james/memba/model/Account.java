package com.james.memba.model;

public class Account {
    private String mUserId;
    private String mUsername;

    public Account(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUsername(String name) {
        mUsername = name;
    }
}
