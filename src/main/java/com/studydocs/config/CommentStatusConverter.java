package com.studydocs.config;

import com.studydocs.model.enums.CommentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CommentStatusConverter implements AttributeConverter<CommentStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(CommentStatus status) {
        return status == null ? null : status.getValue();
    }
    
    @Override
    public CommentStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        
        return switch (dbData.toLowerCase()) {
            case "active" -> CommentStatus.ACTIVE;
            case "deleted" -> CommentStatus.DELETED;
            default -> throw new IllegalArgumentException("Unknown comment status: " + dbData);
        };
    }
}

