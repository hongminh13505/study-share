package com.studydocs.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes
 * Run this to generate a new hash for admin password
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("=====================================");
        System.out.println("Password Hash Generator");
        System.out.println("=====================================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("=====================================");
        System.out.println("\nSQL Update Command:");
        System.out.println("UPDATE users SET password_hash = '" + hash + "' WHERE username = 'admin';");
        System.out.println("=====================================");
        
        // Test verification
        boolean matches = encoder.matches(password, hash);
        System.out.println("\nVerification Test: " + (matches ? "✓ PASSED" : "✗ FAILED"));
        
        // Test với hash cũ
        String oldHash = "$2a$10$xn3LI/AjqicNYZHD0.d.8ew7LqpETxGqL3b0aQ1K7Q6KGvP7xH7HO";
        boolean oldMatches = encoder.matches(password, oldHash);
        System.out.println("Old Hash Test: " + (oldMatches ? "✓ PASSED" : "✗ FAILED"));
        System.out.println("Old Hash: " + oldHash);
    }
}


