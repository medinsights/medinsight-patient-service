package com.medinsights.patient_service.dto.vitalsigns;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for vital signs information
 * Supports US-1.3: Medical History & Follow-up
 */
public record VitalSignsResponse(
        UUID id,
        UUID patientId,
        LocalDateTime measurementDate,
        Integer systolicBP,
        Integer diastolicBP,
        Integer heartRate,
        Double temperature,
        Double weight,
        Double height,
        Double bmi,
        Integer respiratoryRate,
        Integer oxygenSaturation,
        Double bloodGlucose,
        String notes,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
        Instant updatedAt,

        UUID createdBy,
        UUID updatedBy
) {
}
