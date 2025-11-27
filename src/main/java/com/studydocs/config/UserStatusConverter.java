package com.studydocs.config;

import com.studydocs.model.enums.UserStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(UserStatus status) {
        if (status == null) {
            return null;
        }
        return status.getValue(); // "active" or "locked"
    }
    
    @Override
    public UserStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        return switch (dbData.toLowerCase()) {
            case "active" -> UserStatus.ACTIVE;
            case "locked" -> UserStatus.LOCKED;
            default -> throw new IllegalArgumentException("Unknown status: " + dbData);
        };
    }
}

