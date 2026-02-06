package com.medinsights.patient_service.dto.cardiovascular;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for cardiovascular exams
 */
public record CardiovascularExamResponse(
        UUID id,
        UUID patientId,
        String examType,
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime examDate,
        
        String results,
        String interpretation,
        String measuredValues,
        String abnormalities,
        String pdfFile,
        String notes,
        String status,
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant createdAt,
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant updatedAt
) {
}
