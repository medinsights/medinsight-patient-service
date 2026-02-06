package com.medinsights.patient_service.services;

import com.medinsights.patient_service.dto.analysis.MedicalAnalysisCreateRequest;
import com.medinsights.patient_service.dto.analysis.MedicalAnalysisResponse;
import com.medinsights.patient_service.dto.analysis.MedicalAnalysisUpdateRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for MedicalAnalysis management
 * Supports US-1.3, US-1.4: Medical History, Lab Results & Report Generation
 */
public interface MedicalAnalysisService {

    /**
     * Create a new medical analysis for a patient
     *
     * @param patientId Patient UUID
     * @param request   Analysis creation details
     * @param userId    User creating the analysis
     * @return Created analysis response
     */
    MedicalAnalysisResponse create(UUID patientId, MedicalAnalysisCreateRequest request, UUID userId);

    /**
     * Update an existing medical analysis
     *
     * @param analysisId Analysis UUID
     * @param request    Update details
     * @param userId     User updating the analysis
     * @return Updated analysis response
     */
    MedicalAnalysisResponse update(UUID analysisId, MedicalAnalysisUpdateRequest request, UUID userId);

    /**
     * Find medical analysis by ID
     *
     * @param analysisId Analysis UUID
     * @return Analysis response
     */
    MedicalAnalysisResponse findById(UUID analysisId);

    /**
     * Find all analyses for a patient
     *
     * @param patientId Patient UUID
     * @return List of analyses
     */
    List<MedicalAnalysisResponse> findByPatientId(UUID patientId);

    /**
     * Find analyses with alerts/anomalies for a patient
     *
     * @param patientId Patient UUID
     * @return List of analyses with alerts
     */
    List<MedicalAnalysisResponse> findWithAlertsByPatientId(UUID patientId);

    /**
     * Find analyses by date range
     *
     * @param patientId Patient UUID
     * @param startDate Start date
     * @param endDate   End date
     * @return List of analyses
     */
    List<MedicalAnalysisResponse> findByPatientIdAndDateRange(UUID patientId, LocalDate startDate, LocalDate endDate);

    /**
     * Delete a medical analysis
     *
     * @param analysisId Analysis UUID
     */
    void delete(UUID analysisId);

    /**
     * Count analyses for a patient
     *
     * @param patientId Patient UUID
     * @return Count
     */
    long countByPatientId(UUID patientId);
}
