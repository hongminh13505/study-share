package com.studydocs.repository;

import com.studydocs.model.entity.Feedback;
import com.studydocs.model.enums.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    
    Page<Feedback> findByUser_UserId(Integer userId, Pageable pageable);
    
    Page<Feedback> findByStatus(FeedbackStatus status, Pageable pageable);
    
    long countByStatus(FeedbackStatus status);
}

