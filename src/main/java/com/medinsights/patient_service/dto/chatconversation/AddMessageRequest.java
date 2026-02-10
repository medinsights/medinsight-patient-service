package com.medinsights.patient_service.dto.chatconversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for adding a message to conversation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddMessageRequest {
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "user|assistant|system", message = "Role must be: user, assistant, or system")
    private String role;
    
    @NotBlank(message = "Content is required")
    private String content;
}
