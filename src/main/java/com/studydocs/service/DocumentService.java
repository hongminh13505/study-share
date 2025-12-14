package com.studydocs.service;

import com.studydocs.model.entity.Document;
import com.studydocs.model.entity.User;
import com.studydocs.model.enums.DocumentStatus;
import com.studydocs.repository.DocumentRepository;
import com.studydocs.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {
    
    private final DocumentRepository documentRepository;
    private final RatingRepository ratingRepository;
    
    public Document createDocument(Document document) {
        if (document.getStatus() == null) {
            document.setStatus(DocumentStatus.PENDING);
        }
        if (document.getDownloadCount() == null) {
            document.setDownloadCount(0);
        }
        if (document.getViewCount() == null) {
            document.setViewCount(0);
        }
        return documentRepository.save(document);
    }
    
    public Optional<Document> findById(Integer id) {
        Optional<Document> documentOpt = documentRepository.findById(id);
        documentOpt.ifPresent(this::enrichDocumentWithRating);
        return documentOpt;
    }

    public Optional<Document> findDetailedById(Integer id) {
        Optional<Document> documentOpt = documentRepository.findDetailedById(id);
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
        Page<Document> documents = documentRepository.findByUser_UserId(userId, DocumentStatus.DELETED, pageable);
        documents.forEach(this::enrichDocumentWithRating);
        return documents;
    }
    
    public Page<Document> getDocumentsByUserAndFolder(Integer userId, Integer folderId, Pageable pageable) {
        Page<Document> documents;
        if (folderId == null) {
            documents = documentRepository.findByUser_UserIdAndFolderIsNull(userId, DocumentStatus.DELETED, pageable);
        } else {
            documents = documentRepository.findByUser_UserIdAndFolder_FolderId(userId, folderId, DocumentStatus.DELETED, pageable);
        }
        documents.forEach(this::enrichDocumentWithRating);
        return documents;
    }
    
    public Page<Document> getDocumentsByMajor(Integer majorId, Pageable pageable) {
        Page<Document> documents = documentRepository.findBySubject_Major_MajorIdAndStatus(
            majorId, DocumentStatus.APPROVED, pageable);
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
    
    public Page<Document> searchDocumentsWithMajor(String keyword, Integer majorId, String sortBy, Pageable pageable) {
        // Get all matching documents first (with a reasonable limit for sorting)
        Page<Document> allDocuments = documentRepository.searchDocumentsWithMajor(
            DocumentStatus.APPROVED, keyword, majorId, PageRequest.of(0, 10000));
        allDocuments.forEach(this::enrichDocumentWithRating);
        
        List<Document> sortedList = new ArrayList<>(allDocuments.getContent());
        
        // Apply sorting if needed
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "download":
                    sortedList.sort((d1, d2) -> Integer.compare(
                        d2.getDownloadCount() != null ? d2.getDownloadCount() : 0,
                        d1.getDownloadCount() != null ? d1.getDownloadCount() : 0));
                    break;
                case "name":
                    sortedList.sort((d1, d2) -> d1.getDocumentName().compareToIgnoreCase(d2.getDocumentName()));
                    break;
                case "latest":
                    sortedList.sort((d1, d2) -> {
                        if (d1.getApprovedAt() != null && d2.getApprovedAt() != null) {
                            return d2.getApprovedAt().compareTo(d1.getApprovedAt());
                        } else if (d1.getApprovedAt() != null) {
                            return -1;
                        } else if (d2.getApprovedAt() != null) {
                            return 1;
                        } else {
                            return d2.getCreatedAt().compareTo(d1.getCreatedAt());
                        }
                    });
                    break;
            }
        }
        
        // Apply pagination after sorting
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedList.size());
        List<Document> pagedList = start < sortedList.size() ? sortedList.subList(start, end) : new ArrayList<>();
        
        return new PageImpl<>(pagedList, pageable, sortedList.size());
    }
    
    public Page<Document> searchDocumentsWithSort(String keyword, String sortBy, Pageable pageable) {
        // Get all matching documents first (with a reasonable limit for sorting)
        Page<Document> allDocuments = documentRepository.searchDocuments(
            DocumentStatus.APPROVED, keyword, PageRequest.of(0, 10000));
        allDocuments.forEach(this::enrichDocumentWithRating);
        
        List<Document> sortedList = new ArrayList<>(allDocuments.getContent());
        
        // Apply sorting if needed
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "download":
                    sortedList.sort((d1, d2) -> Integer.compare(
                        d2.getDownloadCount() != null ? d2.getDownloadCount() : 0,
                        d1.getDownloadCount() != null ? d1.getDownloadCount() : 0));
                    break;
                case "name":
                    sortedList.sort((d1, d2) -> d1.getDocumentName().compareToIgnoreCase(d2.getDocumentName()));
                    break;
                case "latest":
                    sortedList.sort((d1, d2) -> {
                        if (d1.getApprovedAt() != null && d2.getApprovedAt() != null) {
                            return d2.getApprovedAt().compareTo(d1.getApprovedAt());
                        } else if (d1.getApprovedAt() != null) {
                            return -1;
                        } else if (d2.getApprovedAt() != null) {
                            return 1;
                        } else {
                            return d2.getCreatedAt().compareTo(d1.getCreatedAt());
                        }
                    });
                    break;
            }
        }
        
        // Apply pagination after sorting
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedList.size());
        List<Document> pagedList = start < sortedList.size() ? sortedList.subList(start, end) : new ArrayList<>();
        
        return new PageImpl<>(pagedList, pageable, sortedList.size());
    }
    
    public Page<Document> getDocumentsByMajorWithSort(Integer majorId, String sortBy, Pageable pageable) {
        // Get all documents first (with a reasonable limit for sorting)
        Page<Document> allDocuments = documentRepository.findBySubject_Major_MajorIdAndStatus(
            majorId, DocumentStatus.APPROVED, PageRequest.of(0, 10000));
        allDocuments.forEach(this::enrichDocumentWithRating);
        
        List<Document> sortedList = new ArrayList<>(allDocuments.getContent());
        
        // Apply sorting if needed
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "download":
                    sortedList.sort((d1, d2) -> Integer.compare(
                        d2.getDownloadCount() != null ? d2.getDownloadCount() : 0,
                        d1.getDownloadCount() != null ? d1.getDownloadCount() : 0));
                    break;
                case "name":
                    sortedList.sort((d1, d2) -> d1.getDocumentName().compareToIgnoreCase(d2.getDocumentName()));
                    break;
                case "latest":
                    sortedList.sort((d1, d2) -> {
                        if (d1.getApprovedAt() != null && d2.getApprovedAt() != null) {
                            return d2.getApprovedAt().compareTo(d1.getApprovedAt());
                        } else if (d1.getApprovedAt() != null) {
                            return -1;
                        } else if (d2.getApprovedAt() != null) {
                            return 1;
                        } else {
                            return d2.getCreatedAt().compareTo(d1.getCreatedAt());
                        }
                    });
                    break;
            }
        }
        
        // Apply pagination after sorting
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedList.size());
        List<Document> pagedList = start < sortedList.size() ? sortedList.subList(start, end) : new ArrayList<>();
        
        return new PageImpl<>(pagedList, pageable, sortedList.size());
    }
    
    public Page<Document> getAllDocumentsWithSort(String sortBy, Pageable pageable) {
        // Get all documents first (with a reasonable limit for sorting)
        Page<Document> allDocuments = documentRepository.findByStatus(DocumentStatus.APPROVED, PageRequest.of(0, 10000));
        allDocuments.forEach(this::enrichDocumentWithRating);
        
        List<Document> sortedList = new ArrayList<>(allDocuments.getContent());
        
        // Apply sorting if needed
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "download":
                    sortedList.sort((d1, d2) -> Integer.compare(
                        d2.getDownloadCount() != null ? d2.getDownloadCount() : 0,
                        d1.getDownloadCount() != null ? d1.getDownloadCount() : 0));
                    break;
                case "name":
                    sortedList.sort((d1, d2) -> d1.getDocumentName().compareToIgnoreCase(d2.getDocumentName()));
                    break;
                case "latest":
                    sortedList.sort((d1, d2) -> {
                        if (d1.getApprovedAt() != null && d2.getApprovedAt() != null) {
                            return d2.getApprovedAt().compareTo(d1.getApprovedAt());
                        } else if (d1.getApprovedAt() != null) {
                            return -1;
                        } else if (d2.getApprovedAt() != null) {
                            return 1;
                        } else {
                            return d2.getCreatedAt().compareTo(d1.getCreatedAt());
                        }
                    });
                    break;
            }
        }
        
        // Apply pagination after sorting
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedList.size());
        List<Document> pagedList = start < sortedList.size() ? sortedList.subList(start, end) : new ArrayList<>();
        
        return new PageImpl<>(pagedList, pageable, sortedList.size());
    }
    
    public Page<Document> getLatestDocuments(Pageable pageable) {
        // Get all approved documents first (without limit to sort properly)
        Page<Document> allDocuments = documentRepository.findLatestApprovedDocuments(PageRequest.of(0, 1000));
        allDocuments.forEach(this::enrichDocumentWithRating);
        
        // Sort by approvedAt DESC, then createdAt DESC (approvedAt nulls last)
        List<Document> sortedList = new ArrayList<>(allDocuments.getContent());
        sortedList.sort((d1, d2) -> {
            if (d1.getApprovedAt() != null && d2.getApprovedAt() != null) {
                return d2.getApprovedAt().compareTo(d1.getApprovedAt());
            } else if (d1.getApprovedAt() != null) {
                return -1; // d1 has approvedAt, d2 doesn't - d1 comes first
            } else if (d2.getApprovedAt() != null) {
                return 1; // d2 has approvedAt, d1 doesn't - d2 comes first
            } else {
                // Both null, sort by createdAt DESC
                return d2.getCreatedAt().compareTo(d1.getCreatedAt());
            }
        });
        
        // Apply pagination after sorting
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedList.size());
        List<Document> pagedList = sortedList.subList(start, end);
        
        return new PageImpl<>(
            pagedList, 
            pageable, 
            sortedList.size()
        );
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

