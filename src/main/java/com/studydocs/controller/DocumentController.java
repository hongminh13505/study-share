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
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final FolderService folderService;
    
    @GetMapping("/subjects")
    @ResponseBody
    public java.util.List<com.studydocs.model.entity.Subject> getSubjectsByMajor(
            @RequestParam Integer majorId) {
        return subjectService.getSubjectsByMajor(majorId);
    }
    
    @GetMapping("/view/{id}")
    @Transactional(readOnly = true)
    public String viewDocument(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Model model) {
        
        Document document = documentService.findDetailedById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu!"));
        
        // Check if private document: only owner can view
        if (document.getType() == null && document.getSubject() == null) {
            if (currentUser == null || !currentUser.getUser().getUserId().equals(document.getUser().getUserId())) {
                throw new RuntimeException("Bạn không có quyền xem tài liệu này!");
            }
        } else {
            // Public document must be approved
            if (document.getStatus() != DocumentStatus.APPROVED) {
                throw new RuntimeException("Tài liệu chưa được duyệt!");
            }
        }
        
        // Increment view count
        documentService.incrementViewCount(id);
        
        // Get comments with replies
        var comments = commentService.getCommentsByDocument(id);
        java.util.Map<Integer, java.util.List<com.studydocs.model.entity.Comment>> repliesMap = new java.util.HashMap<>();
        for (var comment : comments) {
            repliesMap.put(comment.getCommentId(), commentService.getRepliesByParentComment(comment.getCommentId()));
        }
        
        model.addAttribute("document", document);
        model.addAttribute("comments", comments);
        model.addAttribute("repliesMap", repliesMap);
        model.addAttribute("commentCount", commentService.countCommentsByDocument(id));
        
        // Check if user has rated this document
        if (currentUser != null) {
            ratingService.getUserRatingForDocument(id, currentUser.getUser().getUserId())
                .ifPresent(rating -> model.addAttribute("userRating", rating.getRatingValue()));
        }
        
        return "documents/view";
    }
    
    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> previewDocument(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Document document = documentService.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu!"));
        
        // Check if private document: only owner can preview
        if (document.getType() == null && document.getSubject() == null) {
            if (currentUser == null || !currentUser.getUser().getUserId().equals(document.getUser().getUserId())) {
                throw new RuntimeException("Bạn không có quyền xem tài liệu này!");
            }
        } else {
            // Public document must be approved
            if (document.getStatus() != DocumentStatus.APPROVED) {
                throw new RuntimeException("Tài liệu chưa được duyệt!");
            }
        }
        
        try {
            Path filePath = fileStorageService.loadFile(document.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                throw new RuntimeException("File không tồn tại!");
            }
            
            // Determine content type based on file extension
            String fileName = document.getFilePath().toLowerCase();
            String contentType = "application/pdf"; // Default
            
            if (fileName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (fileName.endsWith(".doc")) {
                contentType = "application/msword";
            } else if (fileName.endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (fileName.endsWith(".xls")) {
                contentType = "application/vnd.ms-excel";
            } else if (fileName.endsWith(".xlsx")) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if (fileName.endsWith(".ppt")) {
                contentType = "application/vnd.ms-powerpoint";
            } else if (fileName.endsWith(".pptx")) {
                contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            }
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    "inline; filename=\"" + document.getDocumentName() + "\"")
                .body(resource);
                
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tải file: " + e.getMessage());
        }
    }
    
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        Document document = documentService.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu!"));
        
        // Check if document is approved
        if (document.getStatus() != DocumentStatus.APPROVED) {
            throw new RuntimeException("Tài liệu chưa được duyệt!");
        }
        
        // Check if private document: only owner can download
        if (document.getType() == null && document.getSubject() == null) {
            if (currentUser == null || !currentUser.getUser().getUserId().equals(document.getUser().getUserId())) {
                throw new RuntimeException("Bạn không có quyền tải tài liệu này!");
            }
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
            String filename = document.getDocumentName();
            
            // Create ContentDisposition with proper encoding
            ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();
            
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
                
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tải file: " + e.getMessage());
        }
    }
    
    @GetMapping("/upload")
    public String uploadPage(
            @RequestParam(required = false) Integer folderId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Model model) {
        
        model.addAttribute("document", new Document());
        model.addAttribute("majors", majorService.getAllMajors());
        model.addAttribute("documentTypes", documentTypeService.getAllDocumentTypes());
        model.addAttribute("folderId", folderId);
        
        if (currentUser != null) {
            model.addAttribute("folders", folderService.getAllUserFolders(currentUser.getUser()));
        }
        
        return "documents/upload";
    }
    
    @PostMapping("/upload-private")
    public String uploadPrivateDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Integer folderId,
            @RequestParam(required = false) String documentName,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn file!");
            return "redirect:/documents/my-documents";
        }
        
        try {
            // Store file
            String filename = fileStorageService.storeFile(file);
            
            // Create private document (personal, auto-approved, no type/subject)
            Document document = new Document();
            document.setUser(currentUser.getUser());
            document.setDocumentName(documentName != null && !documentName.trim().isEmpty() 
                ? documentName : file.getOriginalFilename());
            document.setFilePath(filename);
            document.setStatus(com.studydocs.model.enums.DocumentStatus.APPROVED); // Auto approve private docs
            document.setDownloadCount(0);
            document.setViewCount(0);
            // Private documents: no type, no subject
            document.setType(null);
            document.setSubject(null);
            
            // Set folder if provided
            if (folderId != null) {
                folderService.getFolderById(folderId).ifPresent(document::setFolder);
            }
            
            documentService.createDocument(document);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Tải lên thành công!");
            
            // Redirect back to folder if uploaded to folder
            if (folderId != null) {
                return "redirect:/documents/my-documents?folderId=" + folderId;
            }
            return "redirect:/documents/my-documents";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/documents/my-documents";
        }
    }
    
    @PostMapping("/upload")
    public String uploadPublicDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Integer majorId,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) String courseCode,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập!");
            return "redirect:/login";
        }
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn file!");
            return "redirect:/documents/upload";
        }
        
        // Validate: Public documents must have major or type
        if (majorId == null && typeId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Vui lòng chọn chuyên ngành hoặc loại tài liệu!");
            return "redirect:/documents/upload";
        }
        
        try {
            // Store file
            String filename = fileStorageService.storeFile(file);
            
            // Create public document (needs approval)
            Document document = new Document();
            document.setUser(currentUser.getUser());
            document.setFilePath(filename);
            // Use original filename as document name
            document.setDocumentName(file.getOriginalFilename());
            document.setStatus(com.studydocs.model.enums.DocumentStatus.PENDING); // Needs approval
            
            // Set course code as tags
            if (courseCode != null && !courseCode.trim().isEmpty()) {
                document.setTags(courseCode.trim().toUpperCase());
            }
            
            // Set subject based on major if provided
            if (majorId != null) {
                var subjects = subjectService.getSubjectsByMajor(majorId);
                com.studydocs.model.entity.Subject selectedSubject = null;
                
                if (!subjects.isEmpty()) {
                    selectedSubject = subjects.get(0);
                } else {
                    // If major has no subjects, create or get a default subject for this major
                    var major = majorService.findById(majorId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên ngành!"));
                    
                    // Check if default subject already exists by searching all subjects
                    String defaultSubjectCode = major.getMajorCode() != null ? 
                        major.getMajorCode() + "_CHUNG" : "MAJOR_" + majorId + "_CHUNG";
                    
                    // Try to find existing default subject by code in all subjects
                    var allSubjects = subjectService.getAllSubjects();
                    for (var subj : allSubjects) {
                        if (subj.getSubjectCode() != null && 
                            subj.getSubjectCode().equals(defaultSubjectCode) &&
                            subj.getMajor().getMajorId().equals(majorId)) {
                            selectedSubject = subj;
                            break;
                        }
                    }
                    
                    // If not found, create new default subject
                    if (selectedSubject == null) {
                        selectedSubject = new com.studydocs.model.entity.Subject();
                        selectedSubject.setMajor(major);
                        selectedSubject.setSubjectName("Tài liệu chung");
                        selectedSubject.setSubjectCode(defaultSubjectCode);
                        selectedSubject.setDescription("Tài liệu chung của " + major.getMajorName());
                        selectedSubject = subjectService.createSubject(selectedSubject);
                    }
                }
                
                document.setSubject(selectedSubject);
            }
            
            // Set type if provided
            if (typeId != null) {
                documentTypeService.findById(typeId).ifPresent(document::setType);
            }
            
            // Public documents should not be in folders
            document.setFolder(null);
            
            documentService.createDocument(document);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Tải lên thành công! Tài liệu đang chờ duyệt từ admin.");
            
            return "redirect:/home";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/documents/upload";
        }
    }
    
    @GetMapping("/my-documents")
    public String myDocuments(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) Integer folderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            Model model) {
        
        User user = currentUser.getUser();
        
        // Get folders (only show root folders if not viewing a specific folder)
        if (folderId == null) {
            model.addAttribute("folders", folderService.getRootFolders(user));
        }
        
        // Get documents by folder
        Page<Document> documents = documentService.getDocumentsByUserAndFolder(
            user.getUserId(), folderId, PageRequest.of(page, size));
        
        model.addAttribute("documents", documents.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", documents.getTotalPages());
        model.addAttribute("currentFolderId", folderId);
        
        return "documents/my-documents";
    }
    
    @PostMapping("/folders/create")
    public String createFolder(
            @RequestParam String folderName,
            @RequestParam(required = false) Integer parentFolderId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        
        try {
            folderService.createFolder(folderName, currentUser.getUser(), parentFolderId);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo folder thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/documents/my-documents";
    }
    
    @PostMapping("/folders/delete/{id}")
    public String deleteFolder(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        try {
            folderService.deleteFolder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa folder thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/documents/my-documents";
    }
    
    @PostMapping("/folders/rename/{id}")
    public String renameFolder(
            @PathVariable Integer id,
            @RequestParam String folderName,
            RedirectAttributes redirectAttributes) {
        
        try {
            folderService.renameFolder(id, folderName);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi tên folder thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/documents/my-documents";
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
    
    @PostMapping("/delete/{id}")
    public String deleteDocument(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(required = false) String redirectUrl,
            RedirectAttributes redirectAttributes) {
        
        try {
            Document document = documentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu!"));
            
            // Check if user is owner or admin
            boolean isOwner = document.getUser().getUserId().equals(currentUser.getUser().getUserId());
            boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!isOwner && !isAdmin) {
                throw new RuntimeException("Bạn không có quyền xóa tài liệu này!");
            }
            
            documentService.deleteDocument(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tài liệu thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        // Redirect to appropriate page
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            return "redirect:" + redirectUrl;
        }
        return "redirect:/documents/my-documents";
    }
    
    // DTO for rating response
    record RatingResponse(Double averageRating, Long ratingCount) {}
}

