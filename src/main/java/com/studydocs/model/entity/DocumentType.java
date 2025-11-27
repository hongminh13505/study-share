package com.studydocs.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer typeId;
    
    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;
    
    @Column(name = "type_code", unique = true, length = 20)
    private String typeCode;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}

