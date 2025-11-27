package com.studydocs.controller;

import com.studydocs.model.entity.Comment;
import com.studydocs.security.CustomUserDetails;
import com.studydocs.service.CommentService;
import com.studydocs.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    private final DocumentService documentService;
    
    @PostMapping("/add")
    public String addComment(
            @RequestParam Integer documentId,
            @RequestParam String content,
            @RequestParam(required = false) Integer parentCommentId,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        
        try {
            Comment comment = new Comment();
            comment.setDocument(documentService.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu!")));
            comment.setUser(currentUser.getUser());
            comment.setContent(content);
            
            if (parentCommentId != null) {
                comment.setParentComment(commentService.findById(parentCommentId)
                    .orElse(null));
            }
            
            commentService.createComment(comment);
            redirectAttributes.addFlashAttribute("successMessage", "Bình luận thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/documents/view/" + documentId;
    }
    
    @PostMapping("/delete/{id}")
    public String deleteComment(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            RedirectAttributes redirectAttributes) {
        
        try {
            Comment comment = commentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bình luận!"));
            
            // Check if user owns the comment or is admin
            if (!comment.getUser().getUserId().equals(currentUser.getUser().getUserId()) 
                && !currentUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new RuntimeException("Bạn không có quyền xóa bình luận này!");
            }
            
            commentService.deleteComment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bình luận thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/documents/view/" + 
            commentService.findById(id).map(c -> c.getDocument().getDocumentId()).orElse(0);
    }
}

