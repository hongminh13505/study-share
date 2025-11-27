package com.studydocs.controller;

import com.studydocs.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    
    private final SubjectService subjectService;
    
    @GetMapping("/subjects/by-major/{majorId}")
    public ResponseEntity<?> getSubjectsByMajor(@PathVariable Integer majorId) {
        return ResponseEntity.ok(subjectService.getSubjectsByMajor(majorId));
    }
}

