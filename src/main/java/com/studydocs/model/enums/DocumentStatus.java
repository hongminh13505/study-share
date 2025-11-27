package com.studydocs.model.enums;

public enum DocumentStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    DELETED("deleted");
    
    private final String value;
    
    DocumentStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}

