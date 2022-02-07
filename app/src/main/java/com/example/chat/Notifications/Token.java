package com.example.chat.Notifications;

import com.google.android.gms.tasks.Task;

public class Token {

    public String token;

    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
