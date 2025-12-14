package com.studydocs.controller;

import com.studydocs.service.DocumentService;
import com.studydocs.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/majors")
@RequiredArgsConstructor
public class MajorController {
    
    private final MajorService majorService;
    private final DocumentService documentService;
    
    @GetMapping
    public String viewMajors(
            @RequestParam(required = false) Integer majorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            Model model) {
        
        // Get all majors to display as folders
        model.addAttribute("majors", majorService.getAllMajors());
        
        // If viewing a specific major, get its documents
        if (majorId != null) {
            var documents = documentService.getDocumentsByMajor(majorId, 
                PageRequest.of(page, size));
            model.addAttribute("documents", documents.getContent());
            model.addAttribute("currentMajorId", majorId);
        }
        
        return "admin/majors";
    }
}

