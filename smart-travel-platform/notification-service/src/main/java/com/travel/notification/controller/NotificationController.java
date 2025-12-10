package com.travel.notification.controller;

import com.travel.notification.dto.NotificationDTO;
import com.travel.notification.dto.NotificationRequestDTO;
import com.travel.notification.dto.NotificationResponseDTO;
import com.travel.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Service", description = "Notification management APIs")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    @Operation(summary = "Send notification")
    public ResponseEntity<NotificationResponseDTO> sendNotification(
            @Valid @RequestBody NotificationRequestDTO requestDTO) {
        log.info("POST /api/notifications/send");
        NotificationDTO notificationDTO = notificationService.sendNotification(requestDTO);
        NotificationResponseDTO response = new NotificationResponseDTO(
                true, "Notification sent successfully", notificationDTO
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user notifications")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long userId) {
        log.info("GET /api/notifications/user/{}", userId);
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable Long id) {
        log.info("GET /api/notifications/{}", id);
        NotificationDTO notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }
}
