package com.travel.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private boolean success;
    private String message;
    private NotificationDTO data;
    private LocalDateTime timestamp;

    public NotificationResponseDTO(boolean success, String message, NotificationDTO data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
