package com.studydocs.config;

import com.studydocs.model.enums.ReportStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReportStatusConverter implements AttributeConverter<ReportStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(ReportStatus status) {
        return status == null ? null : status.getValue();
    }
    
    @Override
    public ReportStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        
        return switch (dbData.toLowerCase()) {
            case "pending" -> ReportStatus.PENDING;
            case "reviewed" -> ReportStatus.REVIEWED;
            case "resolved" -> ReportStatus.RESOLVED;
            case "dismissed" -> ReportStatus.DISMISSED;
            default -> throw new IllegalArgumentException("Unknown report status: " + dbData);
        };
    }
}

