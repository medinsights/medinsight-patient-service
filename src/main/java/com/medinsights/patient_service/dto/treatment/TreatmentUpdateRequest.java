package com.medinsights.patient_service.dto.treatment;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request DTO for updating an existing treatment
 * All fields are optional - only provided fields will be updated
 * Supports US-1.3: Medical History & Follow-up
 */
public record TreatmentUpdateRequest(
        @Size(min = 2, max = 200, message = "Medication name must be between 2 and 200 characters")
        String medicationName,

        @Size(max = 100, message = "Dosage cannot exceed 100 characters")
        String dosage,

        @Size(max = 100, message = "Frequency cannot exceed 100 characters")
        String frequency,

        @Size(max = 50, message = "Route of administration cannot exceed 50 characters")
        String routeOfAdministration,

        LocalDate startDate,

        LocalDate endDate,

        Integer durationDays,

        @Size(max = 20, message = "Status cannot exceed 20 characters")
        String status, // ACTIVE, COMPLETED, DISCONTINUED, PAUSED

        @Size(max = 500, message = "Indication cannot exceed 500 characters")
        String indication,

        @Size(max = 500, message = "Side effects cannot exceed 500 characters")
        String sideEffects,

        @Size(max = 200, message = "Prescriber name cannot exceed 200 characters")
        String prescriberName,

        String notes
) {
    /**
     * Compact constructor for validation and normalization
     */
    public TreatmentUpdateRequest {
        // Trim inputs
        if (medicationName != null) {
            medicationName = medicationName.trim();
        }
        if (dosage != null) {
            dosage = dosage.trim();
        }
        if (status != null) {
            status = status.trim().toUpperCase();
        }
    }
}
