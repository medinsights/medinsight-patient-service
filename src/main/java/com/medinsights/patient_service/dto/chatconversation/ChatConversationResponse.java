package com.medinsights.patient_service.dto.chatconversation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ChatConversation Response DTO
 * Returned when fetching conversation details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatConversationResponse {
    
    private UUID id;
    private UUID patientId;
    private String sessionId;
    private String title;
    private String messages;  // JSON array as string
    private Integer messageCount;
    private LocalDateTime startedAt;
    private LocalDateTime lastMessageAt;
    private String status;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
