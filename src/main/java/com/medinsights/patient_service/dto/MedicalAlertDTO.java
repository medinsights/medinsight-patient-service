package com.medinsights.patient_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for MedicalAlert responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalAlertDTO {
    
    private UUID id;
    
    @NotNull(message = "Patient ID is required")
    private UUID patientId;
    
    @NotBlank(message = "Alert type is required")
    @Size(max = 100, message = "Alert type cannot exceed 100 characters")
    private String alertType;
    
    @Size(max = 20, message = "Severity level cannot exceed 20 characters")
    private String severityLevel;
    
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant resolutionDate;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    private String requiredAction;
    
    private UUID createdBy;
    
    private UUID resolvedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant updatedAt;
}
