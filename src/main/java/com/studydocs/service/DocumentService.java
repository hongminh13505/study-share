package com.studydocs.service;

import com.studydocs.model.entity.Document;
import com.studydocs.model.entity.User;
import com.studydocs.model.enums.DocumentStatus;
import com.studydocs.repository.DocumentRepository;
import com.studydocs.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final RatingRepository ratingRepository;
    
    public Document createDocument(Document document) {
        document.setStatus(DocumentStatus.PENDING);
        document.setDownloadCount(0);
        document.setViewCount(0);
        return documentRepository.save(document);
    }
    
    public Optional<Document> findById(Integer id) {
        Optional<Document> documentOpt = documentRepository.findById(id);
        documentOpt.ifPresent(this::enrichDocumentWithRating);
        return documentOpt;
    }
    
    public Page<Document> getAllDocuments(Pageable pageable) {
        Page<Document> documents = documentRepository.findAll(pageable);
        documents.forEach(this::enrichDocumentWithRating);
        return documents;
    }
    
    public Page<Document> getDocumentsByStatus(DocumentStatus status, Pageable pageable) {
        Page<Document> documents = documentRepository.findByStatus(status, pageable);
        documents.forEach(this::enrichDocumentWithRating);
        return documents;
    }
    
    public Page<Document> getDocumentsByUser(Integer userId, Pageable pageable) {
        Page<Document> documents = documentRepository.findByUser_UserId(userId, pageable);
        documents.forEach(this::enrichDocumentWithRating);
        return documents;
    }
    
    public Page<Document> getDocumentsBySubject(Integer subjectId, Pageable pageable) {
        Page<Document> documents = documentRepository.findByStatusAndSubject_SubjectId(
            DocumentStatus.APPROVED, subjectId, pageable);
        documents.forEach(this::enrichDocumentWithRating);
        return documents;
    }
    
    public Page<Document> searchDocuments(String keyword, Pageable pageable) {
        Page<Document> documents = documentRepository.searchDocuments(
            DocumentStatus.APPROVED, keyword, pageable);
        documents.forEach(this::enrichDocumentWithRating);
        return documents;
    }
    
    public Page<Document> getLatestDocuments(Pageable pageable) {
        Page<Document> documents = documentRepository.findLatestApprovedDocuments(pageable);
        documents.forEach(this::enrichDocumentWithRating);
        return documents;
    }
    
    public Page<Document> getMostDownloadedDocuments(Pageable pageable) {
        Page<Document> documents = documentRepository.findMostDownloadedDocuments(pageable);
        documents.forEach(this::enrichDocumentWithRating);
        return documents;
    }
    
    public Page<Document> getMostViewedDocuments(Pageable pageable) {
        Page<Document> documents = documentRepository.findMostViewedDocuments(pageable);
        documents.forEach(this::enrichDocumentWithRating);
        return documents;
    }
    
    public void approveDocument(Integer documentId, User approver) {
        documentRepository.findById(documentId).ifPresent(document -> {
            document.setStatus(DocumentStatus.APPROVED);
            document.setApprovedBy(approver);
            document.setApprovedAt(LocalDateTime.now());
            documentRepository.save(document);
        });
    }
    
    public void rejectDocument(Integer documentId, String reason) {
        documentRepository.findById(documentId).ifPresent(document -> {
            document.setStatus(DocumentStatus.REJECTED);
            document.setRejectionReason(reason);
            documentRepository.save(document);
        });
    }
    
    public void deleteDocument(Integer documentId) {
        documentRepository.findById(documentId).ifPresent(document -> {
            document.setStatus(DocumentStatus.DELETED);
            documentRepository.save(document);
        });
    }
    
    public void incrementViewCount(Integer documentId) {
        documentRepository.findById(documentId).ifPresent(document -> {
            document.setViewCount(document.getViewCount() + 1);
            documentRepository.save(document);
        });
    }
    
    public void incrementDownloadCount(Integer documentId) {
        documentRepository.findById(documentId).ifPresent(document -> {
            document.setDownloadCount(document.getDownloadCount() + 1);
            documentRepository.save(document);
        });
    }
    
    public long countDocumentsByStatus(DocumentStatus status) {
        return documentRepository.countByStatus(status);
    }
    
    public long countDocumentsByUser(Integer userId) {
        return documentRepository.countByUser_UserId(userId);
    }
    
    private void enrichDocumentWithRating(Document document) {
        Double avgRating = ratingRepository.getAverageRatingByDocumentId(document.getDocumentId());
        Long ratingCount = ratingRepository.getRatingCountByDocumentId(document.getDocumentId());
        document.setAverageRating(avgRating != null ? avgRating : 0.0);
        document.setRatingCount(ratingCount != null ? ratingCount.intValue() : 0);
    }
}

