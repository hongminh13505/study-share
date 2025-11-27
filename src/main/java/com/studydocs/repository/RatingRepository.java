package com.studydocs.repository;

import com.studydocs.model.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    
    Optional<Rating> findByDocument_DocumentIdAndUser_UserId(Integer documentId, Integer userId);
    
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.document.documentId = :documentId")
    Double getAverageRatingByDocumentId(@Param("documentId") Integer documentId);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.document.documentId = :documentId")
    Long getRatingCountByDocumentId(@Param("documentId") Integer documentId);
    
    boolean existsByDocument_DocumentIdAndUser_UserId(Integer documentId, Integer userId);
}

