package com.studydocs.model.enums;

public enum ReportStatus {
    PENDING("pending"),
    REVIEWED("reviewed"),
    RESOLVED("resolved"),
    DISMISSED("dismissed");
    
    private final String value;
    
    ReportStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}

