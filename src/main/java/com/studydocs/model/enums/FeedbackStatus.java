package com.studydocs.model.enums;

public enum FeedbackStatus {
    PENDING("pending"),
    READ("read"),
    REPLIED("replied");
    
    private final String value;
    
    FeedbackStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}

