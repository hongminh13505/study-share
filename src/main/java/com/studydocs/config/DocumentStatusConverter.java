package com.studydocs.config;

import com.studydocs.model.enums.DocumentStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DocumentStatusConverter implements AttributeConverter<DocumentStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(DocumentStatus status) {
        return status == null ? null : status.getValue();
    }
    
    @Override
    public DocumentStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        
        return switch (dbData.toLowerCase()) {
            case "pending" -> DocumentStatus.PENDING;
            case "approved" -> DocumentStatus.APPROVED;
            case "rejected" -> DocumentStatus.REJECTED;
            case "deleted" -> DocumentStatus.DELETED;
            default -> throw new IllegalArgumentException("Unknown document status: " + dbData);
        };
    }
}

