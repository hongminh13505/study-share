package com.studydocs.repository;

import com.studydocs.model.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Integer> {
    Optional<Major> findByMajorCode(String majorCode);
    boolean existsByMajorCode(String majorCode);
}

