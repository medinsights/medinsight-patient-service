package com.medinsights.patient_service.dto.consultation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for consultation response
 * Returns consultation data to client
 */
public record ConsultationResponse(
        UUID id,
        UUID patientId,
        LocalDateTime consultationDate,
        String reasonForVisit,
        String symptoms,
        String physicalExamination,
        String diagnosis,
        String treatment,
        String prescriptions,
        String notes,
        String vitalSigns,
        String followUpInstructions,
        LocalDateTime nextAppointment,
        String status,
        Instant createdAt,
        Instant updatedAt,
        UUID createdBy,
        UUID updatedBy
) {
}
