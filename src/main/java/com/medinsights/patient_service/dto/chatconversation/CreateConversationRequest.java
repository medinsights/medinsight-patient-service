package com.medinsights.patient_service.dto.chatconversation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new conversation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateConversationRequest {
    
    @NotBlank(message = "Session ID is required")
    private String sessionId;
    
    private String title;  // Optional, can be auto-generated
    
    private String tags;   // Optional, comma-separated tags
}
