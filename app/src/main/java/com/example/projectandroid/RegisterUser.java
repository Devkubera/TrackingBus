package com.example.projectandroid;

public class RegisterUser {
    public String email;
    public String userName;
    public String uid;
    public String role;

    public RegisterUser() {}

    public RegisterUser(String email, String username, String uid, String role) {
        this.email = email;
        this.userName = username;
        this.uid = uid;
        this.role = role;
    }

    public RegisterUser(String email, String uid, String role) {
        this.email = email;
        this.uid = uid;
        this.role = role;
    }
}
