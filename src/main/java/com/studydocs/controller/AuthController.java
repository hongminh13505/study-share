package com.studydocs.controller;

import com.studydocs.model.entity.User;
import com.studydocs.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    @GetMapping("/login")
    public String loginPage(
            @ModelAttribute("error") String error,
            @ModelAttribute("logout") String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Đăng xuất thành công!");
        }
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(
            @ModelAttribute("user") User user,
            @ModelAttribute("confirmPassword") String confirmPassword,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        // Validation
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Tên đăng nhập không được để trống!");
            return "auth/register";
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Email không được để trống!");
            return "auth/register";
        }
        
        if (user.getPasswordHash() == null || user.getPasswordHash().length() < 6) {
            model.addAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự!");
            return "auth/register";
        }
        
        if (!user.getPasswordHash().equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp!");
            return "auth/register";
        }
        
        try {
            userService.createUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }
}

