package com.studydocs.service;

import com.studydocs.model.entity.Document;
import com.studydocs.model.entity.Rating;
import com.studydocs.model.entity.User;
import com.studydocs.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {
    
    private final RatingRepository ratingRepository;
    
    public Rating createOrUpdateRating(Document document, User user, Integer ratingValue) {
        if (ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Rating phải từ 1 đến 5");
        }
        
        Optional<Rating> existingRating = ratingRepository.findByDocument_DocumentIdAndUser_UserId(
            document.getDocumentId(), user.getUserId());
        
        Rating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setRatingValue(ratingValue);
        } else {
            rating = new Rating();
            rating.setDocument(document);
            rating.setUser(user);
            rating.setRatingValue(ratingValue);
        }
        
        return ratingRepository.save(rating);
    }
    
    public Optional<Rating> getUserRatingForDocument(Integer documentId, Integer userId) {
        return ratingRepository.findByDocument_DocumentIdAndUser_UserId(documentId, userId);
    }
    
    public Double getAverageRating(Integer documentId) {
        return ratingRepository.getAverageRatingByDocumentId(documentId);
    }
    
    public Long getRatingCount(Integer documentId) {
        return ratingRepository.getRatingCountByDocumentId(documentId);
    }
}

