package com.studydocs.repository;

import com.studydocs.model.entity.Comment;
import com.studydocs.model.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    Page<Comment> findByDocument_DocumentIdAndStatusOrderByCreatedAtDesc(
        Integer documentId, CommentStatus status, Pageable pageable);
    
    List<Comment> findByDocument_DocumentIdAndStatusAndParentCommentIsNullOrderByCreatedAtDesc(
        Integer documentId, CommentStatus status);
    
    List<Comment> findByParentComment_CommentIdAndStatusOrderByCreatedAtAsc(
        Integer parentCommentId, CommentStatus status);
    
    long countByDocument_DocumentIdAndStatus(Integer documentId, CommentStatus status);
}

