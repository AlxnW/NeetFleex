package com.dev.neetfleexui.dto;

public class RequestDetails {
    private String contentName;
    private String userName;
    private ContentType type;

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public RequestDetails( String contentName, String userName, ContentType type) {

        this.contentName = contentName;
        this.userName = userName;
        this.type = type;
    }
}
