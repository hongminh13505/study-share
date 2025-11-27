package com.studydocs.controller;

import com.studydocs.model.entity.Document;
import com.studydocs.model.entity.User;
import com.studydocs.model.enums.DocumentStatus;
import com.studydocs.security.CustomUserDetails;
import com.studydocs.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;

@Controller
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {
    
    private final DocumentService documentService;
    private final MajorService majorService;
    private final SubjectService subjectService;
    private final DocumentTypeService documentTypeService;
    private final FileStorageService fileStorageService;
    private final CommentService commentService;
    private final RatingService ratingService;
    
    @GetMapping("/view/{id}")
    public String viewDocument(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Model model) {
        
        Document document = documentService.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu!"));
        
        // Increment view count
        documentService.incrementViewCount(id);
        
        // Get comments
        model.addAttribute("document", document);
        model.addAttribute("comments", commentService.getCommentsByDocument(id));
        model.addAttribute("commentCount", commentService.countCommentsByDocument(id));
        
        // Check if user has rated this document
        if (currentUser != null) {
            ratingService.getUserRatingForDocument(id, currentUser.getUser().getUserId())
                .ifPresent(rating -> model.addAttribute("userRating", rating.getRatingValue()));
        }
        
        return "documents/view";
    }
    
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Document document = documentService.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu!"));
        
        if (document.getStatus() != DocumentStatus.APPROVED) {
            throw new RuntimeException("Tài liệu chưa được duyệt!");
        }
        
        // Increment download count
        documentService.incrementDownloadCount(id);
        
        try {
            Path filePath = fileStorageService.loadFile(document.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                throw new RuntimeException("File không tồn tại!");
            }
            
            String contentType = "application/octet-stream";
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"" + document.getDocumentName() + "\"")
                .body(resource);
                
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tải file: " + e.getMessage());
        }
    }
    
    @GetMapping("/upload")
    public String uploadPage(Model model) {
        model.addAttribute("document", new Document());
        model.addAttribute("majors", majorService.getAllMajors());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("documentTypes", documentTypeService.getAllDocumentTypes());
        return "documents/upload";
    }
    
    @PostMapping("/upload")
    public String uploadDocument(
            @ModelAttribute Document document,
            @RequestParam("file") MultipartFile file,
            @RequestParam("subjectId") Integer subjectId,
            @RequestParam("typeId") Integer typeId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn file!");
            return "redirect:/documents/upload";
        }
        
        try {
            // Store file
            String filename = fileStorageService.storeFile(file);
            
            // Create document
            document.setUser(currentUser.getUser());
            document.setSubject(subjectService.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học!")));
            document.setType(documentTypeService.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại tài liệu!")));
            document.setFilePath(filename);
            
            documentService.createDocument(document);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Tải lên thành công! Tài liệu đang chờ duyệt.");
            return "redirect:/documents/my-documents";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/documents/upload";
        }
    }
    
    @GetMapping("/my-documents")
    public String myDocuments(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Document> documents = documentService.getDocumentsByUser(
            currentUser.getUser().getUserId(), PageRequest.of(page, size));
        
        model.addAttribute("documents", documents);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", documents.getTotalPages());
        
        return "documents/my-documents";
    }
    
    @PostMapping("/rate/{id}")
    @ResponseBody
    public ResponseEntity<?> rateDocument(
            @PathVariable Integer id,
            @RequestParam Integer rating,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        try {
            Document document = documentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu!"));
            
            ratingService.createOrUpdateRating(document, currentUser.getUser(), rating);
            
            Double avgRating = ratingService.getAverageRating(id);
            Long ratingCount = ratingService.getRatingCount(id);
            
            return ResponseEntity.ok()
                .body(new RatingResponse(avgRating, ratingCount));
                
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // DTO for rating response
    record RatingResponse(Double averageRating, Long ratingCount) {}
}

