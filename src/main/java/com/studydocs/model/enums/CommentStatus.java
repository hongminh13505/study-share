package com.studydocs.model.enums;

public enum CommentStatus {
    ACTIVE("active"),
    DELETED("deleted");
    
    private final String value;
    
    CommentStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}

