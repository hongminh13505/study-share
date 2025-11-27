package com.studydocs.controller;

import com.studydocs.model.entity.Document;
import com.studydocs.model.enums.DocumentStatus;
import com.studydocs.service.DocumentService;
import com.studydocs.service.MajorService;
import com.studydocs.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class HomeController {
    
    private final DocumentService documentService;
    private final MajorService majorService;
    private final SubjectService subjectService;
    
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        Page<Document> latestDocuments = documentService.getLatestDocuments(PageRequest.of(0, 6));
        Page<Document> popularDocuments = documentService.getMostViewedDocuments(PageRequest.of(0, 6));
        
        model.addAttribute("latestDocuments", latestDocuments.getContent());
        model.addAttribute("popularDocuments", popularDocuments.getContent());
        model.addAttribute("majors", majorService.getAllMajors());
        
        return "home";
    }
    
    @GetMapping("/documents/search")
    public String searchDocuments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer majorId,
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        Page<Document> documents;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            documents = documentService.searchDocuments(keyword, PageRequest.of(page, size));
        } else if (subjectId != null) {
            documents = documentService.getDocumentsBySubject(subjectId, PageRequest.of(page, size));
        } else {
            documents = documentService.getDocumentsByStatus(DocumentStatus.APPROVED, PageRequest.of(page, size));
        }
        
        model.addAttribute("documents", documents);
        model.addAttribute("majors", majorService.getAllMajors());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedMajorId", majorId);
        model.addAttribute("selectedSubjectId", subjectId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", documents.getTotalPages());
        
        return "documents/search";
    }
}

