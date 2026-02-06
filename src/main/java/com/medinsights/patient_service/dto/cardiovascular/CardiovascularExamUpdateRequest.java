package com.medinsights.patient_service.dto.cardiovascular;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request DTO for updating cardiovascular exams
 * All fields are optional for partial updates
 */
public record CardiovascularExamUpdateRequest(
        @Size(min = 2, max = 100, message = "Exam type must be between 2 and 100 characters")
        String examType,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime examDate,

        @Size(min = 3, max = 2000, message = "Results must be between 3 and 2000 characters")
        String results,

        @Size(max = 2000, message = "Interpretation cannot exceed 2000 characters")
        String interpretation,

        @Size(max = 1000, message = "Measured values cannot exceed 1000 characters")
        String measuredValues,

        @Size(max = 1000, message = "Abnormalities cannot exceed 1000 characters")
        String abnormalities,

        @Size(max = 500, message = "PDF file path cannot exceed 500 characters")
        String pdfFile,

        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String notes,

        @Size(max = 50, message = "Status cannot exceed 50 characters")
        String status
) {
}
