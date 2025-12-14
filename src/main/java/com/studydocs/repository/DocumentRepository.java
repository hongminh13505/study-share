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
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    
    @Query(value = "SELECT DISTINCT d FROM Document d " +
           "LEFT JOIN FETCH d.user " +
           "LEFT JOIN FETCH d.subject s " +
           "LEFT JOIN FETCH s.major " +
           "LEFT JOIN FETCH d.type " +
           "WHERE d.status = :status AND (d.type IS NOT NULL OR d.subject IS NOT NULL)",
           countQuery = "SELECT COUNT(DISTINCT d) FROM Document d WHERE d.status = :status AND (d.type IS NOT NULL OR d.subject IS NOT NULL)")
    Page<Document> findByStatus(@Param("status") DocumentStatus status, Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.user.userId = :userId AND d.status <> :deletedStatus")
    Page<Document> findByUser_UserId(@Param("userId") Integer userId, 
                                     @Param("deletedStatus") com.studydocs.model.enums.DocumentStatus deletedStatus,
                                     Pageable pageable);
    
    @Query("SELECT d FROM Document d " +
           "LEFT JOIN FETCH d.user " +
           "LEFT JOIN FETCH d.subject " +
           "LEFT JOIN FETCH d.type " +
           "WHERE d.user.userId = :userId AND d.folder.folderId = :folderId " +
           "AND d.type IS NULL AND d.subject IS NULL " +
           "AND d.status <> :deletedStatus")
    Page<Document> findByUser_UserIdAndFolder_FolderId(@Param("userId") Integer userId, 
                                                        @Param("folderId") Integer folderId,
                                                        @Param("deletedStatus") com.studydocs.model.enums.DocumentStatus deletedStatus,
                                                        Pageable pageable);
    
    @Query("SELECT d FROM Document d " +
           "LEFT JOIN FETCH d.user " +
           "LEFT JOIN FETCH d.subject " +
           "LEFT JOIN FETCH d.type " +
           "WHERE d.user.userId = :userId AND d.folder IS NULL " +
           "AND d.type IS NULL AND d.subject IS NULL " +
           "AND d.status <> :deletedStatus")
    Page<Document> findByUser_UserIdAndFolderIsNull(@Param("userId") Integer userId,
                                                      @Param("deletedStatus") com.studydocs.model.enums.DocumentStatus deletedStatus,
                                                      Pageable pageable);
    
    Page<Document> findBySubject_SubjectId(Integer subjectId, Pageable pageable);
    
    @Query("SELECT d FROM Document d " +
           "LEFT JOIN FETCH d.user " +
           "LEFT JOIN FETCH d.subject " +
           "LEFT JOIN FETCH d.type " +
           "WHERE d.subject.major.majorId = :majorId AND d.status = :status " +
           "AND (d.type IS NOT NULL OR d.subject IS NOT NULL)")
    Page<Document> findBySubject_Major_MajorIdAndStatus(@Param("majorId") Integer majorId, 
                                                          @Param("status") DocumentStatus status, 
                                                          Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.type.typeId = :typeId " +
           "AND d.status = 'APPROVED' " +
           "AND (d.type IS NOT NULL OR d.subject IS NOT NULL)")
    Page<Document> findByType_TypeId(@Param("typeId") Integer typeId, Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.status = :status " +
           "AND d.subject.subjectId = :subjectId " +
           "AND (d.type IS NOT NULL OR d.subject IS NOT NULL)")
    Page<Document> findByStatusAndSubject_SubjectId(@Param("status") DocumentStatus status, 
                                                    @Param("subjectId") Integer subjectId, 
                                                    Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.status = :status " +
           "AND (d.type IS NOT NULL OR d.subject IS NOT NULL) " +
           "AND (LOWER(d.documentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Document> searchDocuments(@Param("status") DocumentStatus status, 
                                   @Param("keyword") String keyword, 
                                   Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.status = :status " +
           "AND (d.type IS NOT NULL OR d.subject IS NOT NULL) " +
           "AND (:majorId IS NULL OR d.subject.major.majorId = :majorId) " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(d.documentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Document> searchDocumentsWithMajor(@Param("status") DocumentStatus status,
                                            @Param("keyword") String keyword,
                                            @Param("majorId") Integer majorId,
                                            Pageable pageable);
    
    @Query("SELECT d FROM Document d " +
           "LEFT JOIN FETCH d.user " +
           "LEFT JOIN FETCH d.subject " +
           "LEFT JOIN FETCH d.type " +
           "WHERE d.status = 'APPROVED'")
    Page<Document> findLatestApprovedDocuments(Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE d.status = 'APPROVED' " +
           "AND (d.type IS NOT NULL OR d.subject IS NOT NULL) " +
           "ORDER BY d.downloadCount DESC")
    Page<Document> findMostDownloadedDocuments(Pageable pageable);
    
    @Query("SELECT d FROM Document d " +
           "LEFT JOIN FETCH d.user " +
           "LEFT JOIN FETCH d.subject " +
           "LEFT JOIN FETCH d.type " +
           "WHERE d.status = 'APPROVED' " +
           "ORDER BY d.viewCount DESC")
    Page<Document> findMostViewedDocuments(Pageable pageable);
    
    long countByStatus(DocumentStatus status);
    
    long countByUser_UserId(Integer userId);

    @Query("""
        SELECT d FROM Document d
        LEFT JOIN FETCH d.user
        LEFT JOIN FETCH d.subject
        LEFT JOIN FETCH d.type
        LEFT JOIN FETCH d.folder
        WHERE d.documentId = :id
        """)
    Optional<Document> findDetailedById(@Param("id") Integer id);
}

