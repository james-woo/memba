package com.james.memba.model;

public class Account {
    private String mUserId;

    public Account(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }
}
