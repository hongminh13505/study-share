package com.studydocs.controller;

import com.studydocs.model.entity.Notification;
import com.studydocs.security.CustomUserDetails;
import com.studydocs.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(Map.of("notifications", List.of(), "unreadCount", 0));
        }
        
        List<Notification> notifications = notificationService.getUserNotifications(userDetails.getUser());
        long unreadCount = notificationService.countUnreadNotifications(userDetails.getUser());
        
        List<Map<String, Object>> notificationList = notifications.stream()
            .limit(20)
            .map(n -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", n.getNotificationId());
                map.put("title", n.getTitle());
                map.put("message", n.getMessage());
                map.put("type", n.getType());
                map.put("documentId", n.getDocumentId());
                map.put("isRead", n.getIsRead());
                map.put("createdAt", n.getCreatedAt().toString());
                return map;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notificationList);
        response.put("unreadCount", unreadCount);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unauthorized"));
        }
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
    
    @PostMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unauthorized"));
        }
        notificationService.markAllAsRead(userDetails.getUser());
        return ResponseEntity.ok(Map.of("status", "success"));
    }
}

