package com.example.loginreg;

public class UserSession {
    // Static variable to hold the logged-in user's email
    private static String userEmail;

    // Private constructor to prevent instantiation
    private UserSession() {}

    // Set the user's email when they log in
    public static void setUserEmail(String email) {
        userEmail = email;
    }

    // Get the logged-in user's email
    public static String getUserEmail() {
        return userEmail;
    }

    // Optionally, clear the session when the user logs out
    public static void clearSession() {
        userEmail = null;
    }
}

