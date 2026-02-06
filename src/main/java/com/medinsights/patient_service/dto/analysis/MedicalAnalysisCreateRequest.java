package com.medinsights.patient_service.dto.analysis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating a medical analysis record
 * Supports US-1.3: Medical History & Follow-up
 */
public record MedicalAnalysisCreateRequest(
        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotBlank(message = "Analysis type is required")
        @Size(max = 100, message = "Analysis type cannot exceed 100 characters")
        String analysisType,

        @NotNull(message = "Analysis date is required")
        LocalDate analysisDate,

        @Size(max = 200, message = "File name cannot exceed 200 characters")
        String fileName,

        String ocrText,

        String results,

        String interpretation,

        String alertsAndAnomalies,

        String recommendations,

        @Size(max = 200, message = "Performed by cannot exceed 200 characters")
        String performedBy,

        @Size(max = 200, message = "Interpreted by cannot exceed 200 characters")
        String interpretedBy,

        @NotBlank(message = "Status is required")
        @Size(max = 20, message = "Status cannot exceed 20 characters")
        String status,

        String notes
) {
    /**
     * Compact constructor for validation and normalization
     */
    public MedicalAnalysisCreateRequest {
        if (analysisType != null) {
            analysisType = analysisType.trim().toUpperCase();
        }
        if (status != null) {
            status = status.trim().toUpperCase();
        }
    }
}
