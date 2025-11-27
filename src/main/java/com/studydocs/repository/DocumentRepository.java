package com.studydocs.repository;

import com.studydocs.model.entity.Document;
import com.studydocs.model.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    
    Page<Document> findByStatus(DocumentStatus status, Pageable pageable);
    
    Page<Document> findByUser_UserId(Integer userId, Pageable pageable);
    
    Page<Document> findBySubject_SubjectId(Integer subjectId, Pageable pageable);
    
    Page<Document> findByType_TypeId(Integer typeId, Pageable pageable);
    
    Page<Document> findByStatusAndSubject_SubjectId(DocumentStatus status, Integer subjectId, Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.status = :status AND " +
           "(LOWER(d.documentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Document> searchDocuments(@Param("status") DocumentStatus status, 
                                   @Param("keyword") String keyword, 
                                   Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.status = 'APPROVED' " +
           "ORDER BY d.createdAt DESC")
    Page<Document> findLatestApprovedDocuments(Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.status = 'APPROVED' " +
           "ORDER BY d.downloadCount DESC")
    Page<Document> findMostDownloadedDocuments(Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.status = 'APPROVED' " +
           "ORDER BY d.viewCount DESC")
    Page<Document> findMostViewedDocuments(Pageable pageable);
    
    long countByStatus(DocumentStatus status);
    
    long countByUser_UserId(Integer userId);
}

