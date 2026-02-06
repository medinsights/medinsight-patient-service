package com.medinsights.patient_service.dto.treatment;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for treatment information
 * Supports US-1.3: Medical History & Follow-up
 */
public record TreatmentResponse(
        UUID id,
        UUID patientId,
        String medicationName,
        String dosage,
        String frequency,
        String routeOfAdministration,
        LocalDate startDate,
        LocalDate endDate,
        Integer durationDays,
        String status,
        String indication,
        String sideEffects,
        String prescriberName,
        String notes,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant updatedAt,

        UUID createdBy,
        UUID updatedBy
) {
}
