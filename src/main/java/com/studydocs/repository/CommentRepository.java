package com.studydocs.repository;

import com.studydocs.model.entity.Comment;
import com.studydocs.model.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    Page<Comment> findByDocument_DocumentIdAndStatusOrderByCreatedAtDesc(
        Integer documentId, CommentStatus status, Pageable pageable);
    
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.document.documentId = :documentId " +
           "AND c.status = :status " +
           "AND c.parentComment IS NULL " +
           "ORDER BY c.createdAt DESC")
    List<Comment> findByDocument_DocumentIdAndStatusAndParentCommentIsNullOrderByCreatedAtDesc(
        @Param("documentId") Integer documentId, 
        @Param("status") CommentStatus status);
    
    @Query("SELECT c FROM Comment c " +
           "LEFT JOIN FETCH c.user " +
           "WHERE c.parentComment.commentId = :parentCommentId " +
           "AND c.status = :status " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findByParentComment_CommentIdAndStatusOrderByCreatedAtAsc(
        @Param("parentCommentId") Integer parentCommentId, 
        @Param("status") CommentStatus status);
    
    long countByDocument_DocumentIdAndStatus(Integer documentId, CommentStatus status);
}

