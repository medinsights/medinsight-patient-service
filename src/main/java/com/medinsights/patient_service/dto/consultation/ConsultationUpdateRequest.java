package com.medinsights.patient_service.dto.consultation;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO for updating an existing consultation
 * All fields are optional for partial updates
 */
public record ConsultationUpdateRequest(
        LocalDateTime consultationDate,

        @Size(min = 3, max = 200, message = "Reason must be between 3 and 200 characters")
        String reasonForVisit,

        @Size(max = 1000, message = "Symptoms cannot exceed 1000 characters")
        String symptoms,

        @Size(max = 1000, message = "Physical examination cannot exceed 1000 characters")
        String physicalExamination,

        @Size(max = 1000, message = "Diagnosis cannot exceed 1000 characters")
        String diagnosis,

        @Size(max = 1000, message = "Treatment cannot exceed 1000 characters")
        String treatment,

        @Size(max = 500, message = "Prescriptions cannot exceed 500 characters")
        String prescriptions,

        @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
        String notes,

        @Size(max = 500, message = "Vital signs cannot exceed 500 characters")
        String vitalSigns,

        @Size(max = 200, message = "Follow-up instructions cannot exceed 200 characters")
        String followUpInstructions,

        LocalDateTime nextAppointment,

        @Size(max = 50, message = "Status cannot exceed 50 characters")
        String status
) {
}
