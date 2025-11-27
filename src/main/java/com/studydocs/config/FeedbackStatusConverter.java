package com.studydocs.config;

import com.studydocs.model.enums.FeedbackStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FeedbackStatusConverter implements AttributeConverter<FeedbackStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(FeedbackStatus status) {
        return status == null ? null : status.getValue();
    }
    
    @Override
    public FeedbackStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        
        return switch (dbData.toLowerCase()) {
            case "pending" -> FeedbackStatus.PENDING;
            case "read" -> FeedbackStatus.READ;
            case "replied" -> FeedbackStatus.REPLIED;
            default -> throw new IllegalArgumentException("Unknown feedback status: " + dbData);
        };
    }
}

