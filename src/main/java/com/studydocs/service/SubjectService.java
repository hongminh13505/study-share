package com.studydocs.service;

import com.studydocs.model.entity.Subject;
import com.studydocs.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {
    
    private final SubjectRepository subjectRepository;
    
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
    
    public List<Subject> getSubjectsByMajor(Integer majorId) {
        return subjectRepository.findByMajor_MajorId(majorId);
    }
    
    public Optional<Subject> findById(Integer id) {
        return subjectRepository.findById(id);
    }
    
    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
    
    public Subject updateSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
    
    public void deleteSubject(Integer id) {
        subjectRepository.deleteById(id);
    }
}

