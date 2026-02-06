package com.medinsights.patient_service.dto.analysis;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for medical analysis information
 * Supports US-1.3: Medical History & Follow-up
 */
public record MedicalAnalysisResponse(
        UUID id,
        UUID patientId,
        String analysisType,
        LocalDate analysisDate,
        String fileName,
        String ocrText,
        String results,
        String interpretation,
        String alertsAndAnomalies,
        String recommendations,
        String performedBy,
        String interpretedBy,
        String status,
        String notes,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant updatedAt,

        UUID createdBy,
        UUID updatedBy
) {
}
