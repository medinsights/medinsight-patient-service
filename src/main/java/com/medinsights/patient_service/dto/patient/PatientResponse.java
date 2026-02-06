package com.medinsights.patient_service.dto.patient;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for patient information
 * Supports US-1.1, US-1.2: Unified Patient Management
 */
public record PatientResponse(
        UUID id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        String email,
        String address,
        String city,
        String postalCode,
        String country,
        String bloodGroup,
        String familyHistory,
        String allergies,
        String chronicDiseases,
        String emergencyContactName,
        String emergencyContactPhone,
        String attendingPhysician,
        String notes,
        Boolean active,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant updatedAt,

        UUID createdBy,
        UUID updatedBy
) {
}
