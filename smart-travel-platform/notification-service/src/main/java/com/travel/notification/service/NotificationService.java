package com.travel.notification.service;

import com.travel.notification.dto.NotificationDTO;
import com.travel.notification.dto.NotificationRequestDTO;
import com.travel.notification.entity.Notification;
import com.travel.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public NotificationDTO sendNotification(NotificationRequestDTO requestDTO) {
        log.info("Sending notification to user {}: {}", requestDTO.getUserId(), requestDTO.getMessage());
        
        Notification notification = new Notification();
        notification.setUserId(requestDTO.getUserId());
        notification.setMessage(requestDTO.getMessage());
        notification.setType(requestDTO.getType() != null ? requestDTO.getType() : "EMAIL");
        
        // Simulate sending notification (90% success rate)
        boolean success = Math.random() > 0.1;
        notification.setStatus(success ? "SENT" : "FAILED");
        
        // Log the notification (simulating actual sending)
        if (success) {
            log.info("✉️ NOTIFICATION SENT [{}] to User {}: {}", 
                    notification.getType(), requestDTO.getUserId(), requestDTO.getMessage());
        } else {
            log.error("❌ NOTIFICATION FAILED to User {}", requestDTO.getUserId());
        }
        
        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        log.info("Fetching notifications for user {}", userId);
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        return convertToDTO(notification);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getId(),
                notification.getUserId(),
                notification.getMessage(),
                notification.getType(),
                notification.getStatus(),
                notification.getSentAt()
        );
    }
}
