package com.studydocs.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "majors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Major {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "major_id")
    private Integer majorId;
    
    @Column(name = "major_name", nullable = false, length = 100)
    private String majorName;
    
    @Column(name = "major_code", unique = true, length = 20)
    private String majorCode;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}

