package com.studydocs.config;

import com.studydocs.model.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, String> {
    
    @Override
    public String convertToDatabaseColumn(UserRole role) {
        if (role == null) {
            return null;
        }
        return role.getValue(); // "admin" or "user"
    }
    
    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        return switch (dbData.toLowerCase()) {
            case "admin" -> UserRole.ADMIN;
            case "user" -> UserRole.USER;
            default -> throw new IllegalArgumentException("Unknown role: " + dbData);
        };
    }
}

