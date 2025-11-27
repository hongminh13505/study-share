package com.studydocs.service;

import com.studydocs.model.entity.Major;
import com.studydocs.repository.MajorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MajorService {
    
    private final MajorRepository majorRepository;
    
    public List<Major> getAllMajors() {
        return majorRepository.findAll();
    }
    
    public Optional<Major> findById(Integer id) {
        return majorRepository.findById(id);
    }
    
    public Major createMajor(Major major) {
        return majorRepository.save(major);
    }
    
    public Major updateMajor(Major major) {
        return majorRepository.save(major);
    }
    
    public void deleteMajor(Integer id) {
        majorRepository.deleteById(id);
    }
}

