package com.medinsights.patient_service.dto.consultation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO for creating a new consultation
 * Matches chatbot schema: date_consultation, motif, examen_clinique, diagnostic, prescription, remarques, prochain_rdv
 */
public record ConsultationCreateRequest(
        @NotNull(message = "Consultation date is required")
        LocalDateTime consultationDate,

        @NotBlank(message = "Reason for visit is required")
        @Size(min = 3, max = 200, message = "Reason must be between 3 and 200 characters")
        String reasonForVisit, // motif from chatbot

        @Size(max = 1000, message = "Symptoms cannot exceed 1000 characters")
        String symptoms,

        @Size(max = 1000, message = "Physical examination cannot exceed 1000 characters")
        String physicalExamination, // examen_clinique from chatbot

        @Size(max = 1000, message = "Diagnosis cannot exceed 1000 characters")
        String diagnosis, // diagnostic from chatbot

        @Size(max = 1000, message = "Treatment cannot exceed 1000 characters")
        String treatment,

        @Size(max = 500, message = "Prescriptions cannot exceed 500 characters")
        String prescriptions, // prescription from chatbot

        @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
        String notes, // remarques from chatbot

        @Size(max = 500, message = "Vital signs cannot exceed 500 characters")
        String vitalSigns,

        @Size(max = 200, message = "Follow-up instructions cannot exceed 200 characters")
        String followUpInstructions,

        LocalDateTime nextAppointment, // prochain_rdv from chatbot

        @Size(max = 50, message = "Status cannot exceed 50 characters")
        String status // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
) {
}
