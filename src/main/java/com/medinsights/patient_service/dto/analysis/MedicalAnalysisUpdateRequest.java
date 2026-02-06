package com.medinsights.patient_service.dto.analysis;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request DTO for updating a medical analysis record
 * All fields are optional - only provided fields will be updated
 * Supports US-1.3: Medical History & Follow-up
 */
public record MedicalAnalysisUpdateRequest(
        @Size(max = 100, message = "Analysis type cannot exceed 100 characters")
        String analysisType,

        LocalDate analysisDate,

        String ocrText,

        String results,

        String interpretation,

        String alertsAndAnomalies,

        String recommendations,

        @Size(max = 200, message = "Performed by cannot exceed 200 characters")
        String performedBy,

        @Size(max = 200, message = "Interpreted by cannot exceed 200 characters")
        String interpretedBy,

        @Size(max = 50, message = "Status cannot exceed 50 characters")
        String status,

        String notes
) {
    /**
     * Compact constructor for validation and normalization
     */
    public MedicalAnalysisUpdateRequest {
        if (analysisType != null) {
            analysisType = analysisType.trim().toUpperCase();
        }
        if (status != null) {
            status = status.trim().toUpperCase();
        }
    }
}
