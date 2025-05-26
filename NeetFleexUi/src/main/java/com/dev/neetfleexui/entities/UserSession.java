package com.dev.neetfleexui.entities;

public class UserSession {
    private String jwtToken;
    private String name;

    public String getName() {
        return name;
    }

    public UserSession(String jwtToken, String name) {
        this.jwtToken = jwtToken;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
