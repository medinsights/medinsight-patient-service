package com.medinsights.patient_service.services;

import com.medinsights.patient_service.dto.treatment.TreatmentCreateRequest;
import com.medinsights.patient_service.dto.treatment.TreatmentResponse;
import com.medinsights.patient_service.dto.treatment.TreatmentUpdateRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for Treatment management
 * Supports US-1.3: Medical History & Follow-up Management
 */
public interface TreatmentService {

    /**
     * Create a new treatment for a patient
     *
     * @param patientId Patient UUID
     * @param request   Treatment creation details
     * @param userId    User creating the treatment
     * @return Created treatment response
     */
    TreatmentResponse create(UUID patientId, TreatmentCreateRequest request, UUID userId);

    /**
     * Update an existing treatment
     *
     * @param treatmentId Treatment UUID
     * @param request     Update details
     * @param userId      User updating the treatment
     * @return Updated treatment response
     */
    TreatmentResponse update(UUID treatmentId, TreatmentUpdateRequest request, UUID userId);

    /**
     * Find treatment by ID
     *
     * @param treatmentId Treatment UUID
     * @return Treatment response
     */
    TreatmentResponse findById(UUID treatmentId);

    /**
     * Find all treatments for a patient
     *
     * @param patientId Patient UUID
     * @return List of treatments
     */
    List<TreatmentResponse> findByPatientId(UUID patientId);

    /**
     * Find active treatments for a patient
     *
     * @param patientId Patient UUID
     * @return List of active treatments
     */
    List<TreatmentResponse> findActiveByPatientId(UUID patientId);

    /**
     * Find treatments by date range
     *
     * @param patientId Patient UUID
     * @param startDate Start date
     * @param endDate   End date
     * @return List of treatments
     */
    List<TreatmentResponse> findByPatientIdAndDateRange(UUID patientId, LocalDate startDate, LocalDate endDate);

    /**
     * Delete a treatment
     *
     * @param treatmentId Treatment UUID
     */
    void delete(UUID treatmentId);

    /**
     * Count treatments for a patient
     *
     * @param patientId Patient UUID
     * @return Count
     */
    long countByPatientId(UUID patientId);
}
