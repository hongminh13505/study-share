package com.studydocs.service;

import com.studydocs.model.entity.DocumentType;
import com.studydocs.repository.DocumentTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentTypeService {
    
    private final DocumentTypeRepository documentTypeRepository;
    
    public List<DocumentType> getAllDocumentTypes() {
        return documentTypeRepository.findAll();
    }
    
    public Optional<DocumentType> findById(Integer id) {
        return documentTypeRepository.findById(id);
    }
    
    public DocumentType createDocumentType(DocumentType documentType) {
        return documentTypeRepository.save(documentType);
    }
}

