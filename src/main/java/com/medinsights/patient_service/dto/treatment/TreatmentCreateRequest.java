package com.medinsights.patient_service.dto.treatment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating a new treatment
 * Supports US-1.3: Medical History & Follow-up
 */
public record TreatmentCreateRequest(
        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotBlank(message = "Medication name is required")
        @Size(min = 2, max = 200, message = "Medication name must be between 2 and 200 characters")
        String medicationName,

        @NotBlank(message = "Dosage is required")
        @Size(max = 100, message = "Dosage cannot exceed 100 characters")
        String dosage,

        @Size(max = 100, message = "Frequency cannot exceed 100 characters")
        String frequency,

        @Size(max = 50, message = "Route of administration cannot exceed 50 characters")
        String routeOfAdministration,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        LocalDate endDate,

        Integer durationDays,

        @Size(max = 500, message = "Indication cannot exceed 500 characters")
        String indication,

        @Size(max = 500, message = "Side effects cannot exceed 500 characters")
        String sideEffects,

        @Size(max = 200, message = "Prescriber name cannot exceed 200 characters")
        String prescriberName,

        @NotBlank(message = "Status is required")
        @Size(max = 20, message = "Status cannot exceed 20 characters")
        String status,

        String notes
) {
    /**
     * Compact constructor for validation and normalization
     */
    public TreatmentCreateRequest {
        // Trim and normalize inputs
        if (medicationName != null) {
            medicationName = medicationName.trim();
        }
        if (dosage != null) {
            dosage = dosage.trim();
        }
        if (routeOfAdministration == null || routeOfAdministration.isBlank()) {
            routeOfAdministration = "ORAL";
        }
    }
}
