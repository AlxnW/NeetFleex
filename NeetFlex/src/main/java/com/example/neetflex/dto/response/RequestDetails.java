package com.example.neetflex.dto.response;

import com.example.neetflex.enums.ContentType;
import lombok.Data;

@Data
public class RequestDetails {
    private String contentName;
    private String userName;
    private ContentType type;




}
