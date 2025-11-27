package com.studydocs.controller;

import com.studydocs.model.enums.DocumentStatus;
import com.studydocs.security.CustomUserDetails;
import com.studydocs.service.DocumentService;
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
    public String pendingDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        var documents = documentService.getDocumentsByStatus(
            DocumentStatus.PENDING, PageRequest.of(page, size));
        
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
            documentService.approveDocument(id, currentUser.getUser());
            redirectAttributes.addFlashAttribute("successMessage", "Duyệt tài liệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/admin/documents/pending";
    }
    
    @PostMapping("/documents/reject/{id}")
    public String rejectDocument(
            @PathVariable Integer id,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {
        
        try {
            documentService.rejectDocument(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Từ chối tài liệu thành công!");
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
}

