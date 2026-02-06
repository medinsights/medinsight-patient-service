package com.medinsights.patient_service.dto.vitalsigns;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for recording vital signs
 * Supports US-1.3: Medical History & Follow-up
 */
public record VitalSignsCreateRequest(
        @NotNull(message = "Patient ID is required")
        UUID patientId,

        @NotNull(message = "Measurement date is required")
        LocalDateTime measurementDate,

        @Min(value = 50, message = "Systolic BP must be at least 50 mmHg")
        @Max(value = 250, message = "Systolic BP cannot exceed 250 mmHg")
        Integer systolicBP,

        @Min(value = 30, message = "Diastolic BP must be at least 30 mmHg")
        @Max(value = 150, message = "Diastolic BP cannot exceed 150 mmHg")
        Integer diastolicBP,

        @Min(value = 30, message = "Heart rate must be at least 30 bpm")
        @Max(value = 250, message = "Heart rate cannot exceed 250 bpm")
        Integer heartRate,

        @Min(value = 30, message = "Temperature must be at least 30°C")
        @Max(value = 45, message = "Temperature cannot exceed 45°C")
        Double temperature,

        @Positive(message = "Weight must be positive")
        Double weight,

        @Positive(message = "Height must be positive")
        Double height,

        @Min(value = 5, message = "Respiratory rate must be at least 5/min")
        @Max(value = 60, message = "Respiratory rate cannot exceed 60/min")
        Integer respiratoryRate,

        @Min(value = 50, message = "Oxygen saturation must be at least 50%")
        @Max(value = 100, message = "Oxygen saturation cannot exceed 100%")
        Integer oxygenSaturation,

        @Positive(message = "Blood glucose must be positive")
        Double bloodGlucose,

        String notes
) {
}
