package com.studydocs.controller;

import com.studydocs.model.entity.Document;
import com.studydocs.model.entity.Major;
import com.studydocs.model.entity.Subject;
import com.studydocs.model.enums.DocumentStatus;
import com.studydocs.security.CustomUserDetails;
import com.studydocs.service.DocumentService;
import com.studydocs.service.MajorService;
import com.studydocs.service.NotificationService;
import com.studydocs.service.SubjectService;
import com.studydocs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private final DocumentService documentService;
    private final UserService userService;
    private final MajorService majorService;
    private final SubjectService subjectService;
    private final NotificationService notificationService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long pendingDocs = documentService.countDocumentsByStatus(DocumentStatus.PENDING);
        long approvedDocs = documentService.countDocumentsByStatus(DocumentStatus.APPROVED);
        long rejectedDocs = documentService.countDocumentsByStatus(DocumentStatus.REJECTED);
        long activeUsers = userService.countActiveUsers();
        
        model.addAttribute("pendingDocs", pendingDocs);
        model.addAttribute("approvedDocs", approvedDocs);
        model.addAttribute("rejectedDocs", rejectedDocs);
        model.addAttribute("activeUsers", activeUsers);
        
        return "admin/dashboard";
    }
    
    
    @GetMapping("/documents/pending")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String pendingDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        var documents = documentService.getDocumentsByStatus(
            DocumentStatus.PENDING, PageRequest.of(page, size));
        
        // Force initialization of lazy-loaded associations
        documents.getContent().forEach(doc -> {
            if (doc.getUser() != null) doc.getUser().getFullName();
            if (doc.getSubject() != null) {
                doc.getSubject().getSubjectName();
                if (doc.getSubject().getMajor() != null) {
                    doc.getSubject().getMajor().getMajorName();
                }
            }
            if (doc.getType() != null) doc.getType().getTypeName();
        });
        
        model.addAttribute("documents", documents);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", documents.getTotalPages());
        
        return "admin/pending-documents";
    }
    
    @PostMapping("/documents/approve/{id}")
    public String approveDocument(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        
        try {
            var documentOpt = documentService.findById(id);
            if (documentOpt.isPresent()) {
                Document document = documentOpt.get();
                documentService.approveDocument(id, currentUser.getUser());
                
                // Tạo thông báo cho người dùng
                if (document.getUser() != null) {
                    notificationService.createNotification(
                        document.getUser(),
                        "Tài liệu đã được duyệt",
                        "Tài liệu \"" + document.getDocumentName() + "\" của bạn đã được duyệt và công khai.",
                        "APPROVED",
                        document.getDocumentId()
                    );
                }
                
                redirectAttributes.addFlashAttribute("successMessage", "Duyệt tài liệu thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài liệu!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/documents/pending";
    }
    
    @PostMapping("/documents/reject/{id}")
    public String rejectDocument(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "Tài liệu không phù hợp") String reason,
            RedirectAttributes redirectAttributes) {
        
        try {
            var documentOpt = documentService.findById(id);
            if (documentOpt.isPresent()) {
                Document document = documentOpt.get();
                
                // Tạo thông báo cho người dùng trước khi reject
                if (document.getUser() != null) {
                    notificationService.createNotification(
                        document.getUser(),
                        "Tài liệu bị từ chối",
                        "Tài liệu \"" + document.getDocumentName() + "\" của bạn đã bị từ chối. Lý do: " + reason,
                        "REJECTED",
                        document.getDocumentId()
                    );
                }
                
                documentService.rejectDocument(id, reason);
                redirectAttributes.addFlashAttribute("successMessage", "Từ chối đăng tải tài liệu công khai thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài liệu!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/documents/pending";
    }
    
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }
    
    @PostMapping("/users/lock/{id}")
    public String lockUser(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        try {
            userService.lockUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Khóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/unlock/{id}")
    public String unlockUser(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        try {
            userService.unlockUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Mở khóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/promote/{id}")
    public String promoteToAdmin(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        try {
            userService.promoteToAdmin(id);
            redirectAttributes.addFlashAttribute("successMessage", "Nâng cấp lên Admin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/demote/{id}")
    public String demoteToUser(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        try {
            userService.demoteToUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Hạ xuống User thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }
    
    @GetMapping("/majors")
    public String manageMajors(
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
    
    @GetMapping("/majors/subjects")
    @ResponseBody
    public java.util.List<Subject> getSubjectsByMajor(
            @RequestParam Integer majorId) {
        return subjectService.getSubjectsByMajor(majorId);
    }
    
    @PostMapping("/majors/create")
    public String createMajor(
            @ModelAttribute Major major,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Check if major code already exists
            if (majorService.getAllMajors().stream()
                    .anyMatch(m -> m.getMajorCode().equals(major.getMajorCode()))) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mã chuyên ngành đã tồn tại!");
                return "redirect:/admin/majors";
            }
            
            majorService.createMajor(major);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo chuyên ngành thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/majors";
    }
    
    @PostMapping("/majors/update/{id}")
    public String updateMajor(
            @PathVariable Integer id,
            @ModelAttribute Major major,
            RedirectAttributes redirectAttributes) {
        
        try {
            major.setMajorId(id);
            majorService.updateMajor(major);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chuyên ngành thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/majors";
    }
    
    @PostMapping("/majors/delete/{id}")
    public String deleteMajor(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        try {
            majorService.deleteMajor(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chuyên ngành thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: Không thể xóa chuyên ngành này!");
        }
        
        return "redirect:/admin/majors";
    }
}

