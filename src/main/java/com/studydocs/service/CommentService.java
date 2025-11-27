package com.studydocs.service;

import com.studydocs.model.entity.Comment;
import com.studydocs.model.enums.CommentStatus;
import com.studydocs.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    
    private final CommentRepository commentRepository;
    
    public Comment createComment(Comment comment) {
        comment.setStatus(CommentStatus.ACTIVE);
        return commentRepository.save(comment);
    }
    
    public List<Comment> getCommentsByDocument(Integer documentId) {
        return commentRepository.findByDocument_DocumentIdAndStatusAndParentCommentIsNullOrderByCreatedAtDesc(
            documentId, CommentStatus.ACTIVE);
    }
    
    public List<Comment> getRepliesByParentComment(Integer parentCommentId) {
        return commentRepository.findByParentComment_CommentIdAndStatusOrderByCreatedAtAsc(
            parentCommentId, CommentStatus.ACTIVE);
    }
    
    public Optional<Comment> findById(Integer id) {
        return commentRepository.findById(id);
    }
    
    public void deleteComment(Integer commentId) {
        commentRepository.findById(commentId).ifPresent(comment -> {
            comment.setStatus(CommentStatus.DELETED);
            commentRepository.save(comment);
        });
    }
    
    public long countCommentsByDocument(Integer documentId) {
        return commentRepository.countByDocument_DocumentIdAndStatus(documentId, CommentStatus.ACTIVE);
    }
}

