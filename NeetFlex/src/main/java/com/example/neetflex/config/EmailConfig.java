package com.example.neetflex.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for email settings.
 * This class loads email configuration properties from application.properties.
 */
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol = "smtp";
    private boolean auth = true;
    private boolean starttlsEnable = true;
    private String defaultSender;

    // Getters and setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isStarttlsEnable() {
        return starttlsEnable;
    }

    public void setStarttlsEnable(boolean starttlsEnable) {
        this.starttlsEnable = starttlsEnable;
    }

    public String getDefaultSender() {
        return defaultSender;
    }

    public void setDefaultSender(String defaultSender) {
        this.defaultSender = defaultSender;
    }
}
