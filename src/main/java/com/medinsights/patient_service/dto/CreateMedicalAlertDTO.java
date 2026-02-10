package com.medinsights.patient_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for creating medical alerts
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicalAlertDTO {
    
    @NotNull(message = "Patient ID is required")
    private UUID patientId;
    
    @NotBlank(message = "Alert type is required")
    @Size(max = 100, message = "Alert type cannot exceed 100 characters")
    private String alertType;
    
    @Size(max = 20, message = "Severity level cannot exceed 20 characters")
    private String severityLevel;
    
    private String description;
    
    private String requiredAction;
}
