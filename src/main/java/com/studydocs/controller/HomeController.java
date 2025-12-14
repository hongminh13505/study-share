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
        // Dùng cùng logic với trang tìm kiếm: lấy tài liệu đã duyệt
        Page<Document> approvedDocuments = documentService.getDocumentsByStatus(DocumentStatus.APPROVED, PageRequest.of(0, 100));
        
        // Sắp xếp theo approvedAt DESC (duyệt sau ở đầu), lấy 10 tài liệu
        java.util.List<Document> latestDocuments = approvedDocuments.getContent().stream()
            .sorted((d1, d2) -> {
                if (d1.getApprovedAt() != null && d2.getApprovedAt() != null) {
                    return d2.getApprovedAt().compareTo(d1.getApprovedAt());
                } else if (d1.getApprovedAt() != null) {
                    return -1;
                } else if (d2.getApprovedAt() != null) {
                    return 1;
                } else {
                    return d2.getCreatedAt().compareTo(d1.getCreatedAt());
                }
            })
            .limit(10)
            .collect(java.util.stream.Collectors.toList());
        
        // Tài liệu phổ biến: sắp xếp theo lượt tải (downloadCount)
        java.util.List<Document> popularDocuments = approvedDocuments.getContent().stream()
            .sorted((d1, d2) -> Integer.compare(
                d2.getDownloadCount() != null ? d2.getDownloadCount() : 0,
                d1.getDownloadCount() != null ? d1.getDownloadCount() : 0))
            .limit(10)
            .collect(java.util.stream.Collectors.toList());
        
        // Tài liệu yêu thích: sắp xếp theo đánh giá cao nhất (averageRating)
        java.util.List<Document> topRatedDocuments = approvedDocuments.getContent().stream()
            .sorted((d1, d2) -> Double.compare(
                d2.getAverageRating() != null ? d2.getAverageRating() : 0.0,
                d1.getAverageRating() != null ? d1.getAverageRating() : 0.0))
            .limit(10)
            .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("latestDocuments", latestDocuments);
        model.addAttribute("popularDocuments", popularDocuments);
        model.addAttribute("topRatedDocuments", topRatedDocuments);
        model.addAttribute("majors", majorService.getAllMajors());
        
        return "home";
    }
    
    @GetMapping("/documents/search")
    public String searchDocuments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer majorId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        Page<Document> documents;
        
        // If keyword is provided, search with keyword and optional major filter
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (majorId != null) {
                // Search with major filter and sort
                documents = documentService.searchDocumentsWithMajor(keyword, majorId, sortBy, PageRequest.of(page, size));
            } else {
                // Search without major filter - use new method with sort
                documents = documentService.searchDocumentsWithSort(keyword, sortBy, PageRequest.of(page, size));
            }
        } 
        // If major is selected, filter by major
        else if (majorId != null) {
            documents = documentService.getDocumentsByMajorWithSort(majorId, sortBy, PageRequest.of(page, size));
        } 
        // Otherwise, get all approved documents
        else {
            documents = documentService.getAllDocumentsWithSort(sortBy, PageRequest.of(page, size));
        }
        
        model.addAttribute("documents", documents);
        model.addAttribute("majors", majorService.getAllMajors());
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("selectedMajorId", majorId);
        model.addAttribute("selectedSortBy", sortBy);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", documents.getTotalPages());
        
        return "documents/search";
    }
}

