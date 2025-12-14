package com.studydocs.controller;

import com.studydocs.model.entity.User;
import com.studydocs.repository.UserRepository;
import com.studydocs.security.CustomUserDetails;
import com.studydocs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    
    @GetMapping
    public String profilePage(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        User user = currentUser.getUser();
        model.addAttribute("user", user);
        return "profile";
    }
    
    @PostMapping("/update")
    public String updateProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam String fullName,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = userService.findById(currentUser.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
            
            // Check if email is already taken by another user
            if (!user.getEmail().equals(email)) {
                if (userService.findByEmail(email).isPresent()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Email đã được sử dụng bởi người dùng khác!");
                    return "redirect:/profile";
                }
            }
            
            user.setFullName(fullName);
            user.setEmail(email);
            userService.updateUser(user);
            
            // Update current user in session
            currentUser.getUser().setFullName(fullName);
            currentUser.getUser().setEmail(email);
            
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }
    
    @PostMapping("/change-password")
    public String changePassword(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = userService.findById(currentUser.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
            
            // Validate current password
            if (!userService.checkPassword(currentPassword, user.getPasswordHash())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu hiện tại không đúng!");
                return "redirect:/profile";
            }
            
            // Validate new password
            if (newPassword == null || newPassword.trim().isEmpty() || newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới phải có ít nhất 6 ký tự!");
                return "redirect:/profile";
            }
            
            // Validate confirm password
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
                return "redirect:/profile";
            }
            
            // Update password
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }
}

