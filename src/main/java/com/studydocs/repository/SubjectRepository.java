package com.studydocs.repository;

import com.studydocs.model.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    List<Subject> findByMajor_MajorId(Integer majorId);
    List<Subject> findBySubjectNameContainingIgnoreCase(String subjectName);
}

