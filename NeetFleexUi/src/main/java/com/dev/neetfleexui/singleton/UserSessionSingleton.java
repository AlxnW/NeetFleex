package com.dev.neetfleexui.singleton;

import com.dev.neetfleexui.entities.UserSession;

public class UserSessionSingleton {

    private static volatile UserSession instance;

    private UserSessionSingleton() {
        // private constructor to prevent instantiation
    }

    public static UserSession getInstance(String authToken, String name) {
        if (instance == null) {
            synchronized (UserSessionSingleton.class) {
                if (instance == null) {
                    instance = new UserSession(authToken, name);
                }
            }
        }
        return instance;
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }
}
