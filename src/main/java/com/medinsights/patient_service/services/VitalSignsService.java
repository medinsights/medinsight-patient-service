package com.medinsights.patient_service.services;

import com.medinsights.patient_service.dto.vitalsigns.VitalSignsCreateRequest;
import com.medinsights.patient_service.dto.vitalsigns.VitalSignsResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for VitalSigns management
 * Supports US-1.3: Medical History & Follow-up Management
 */
public interface VitalSignsService {

    /**
     * Record new vital signs for a patient
     *
     * @param patientId Patient UUID
     * @param request   Vital signs data
     * @param userId    User recording the vital signs
     * @return Created vital signs response
     */
    VitalSignsResponse create(UUID patientId, VitalSignsCreateRequest request, UUID userId);

    /**
     * Find vital signs by ID
     *
     * @param vitalSignsId VitalSigns UUID
     * @return Vital signs response
     */
    VitalSignsResponse findById(UUID vitalSignsId);

    /**
     * Find all vital signs for a patient
     *
     * @param patientId Patient UUID
     * @return List of vital signs
     */
    List<VitalSignsResponse> findByPatientId(UUID patientId);

    /**
     * Get latest vital signs for a patient
     *
     * @param patientId Patient UUID
     * @return Latest vital signs if available
     */
    Optional<VitalSignsResponse> findLatestByPatientId(UUID patientId);

    /**
     * Find vital signs by date range
     *
     * @param patientId Patient UUID
     * @param startDate Start date
     * @param endDate   End date
     * @return List of vital signs
     */
    List<VitalSignsResponse> findByPatientIdAndDateRange(UUID patientId, LocalDate startDate, LocalDate endDate);

    /**
     * Delete vital signs record
     *
     * @param vitalSignsId VitalSigns UUID
     */
    void delete(UUID vitalSignsId);

    /**
     * Count vital signs records for a patient
     *
     * @param patientId Patient UUID
     * @return Count
     */
    long countByPatientId(UUID patientId);
}
