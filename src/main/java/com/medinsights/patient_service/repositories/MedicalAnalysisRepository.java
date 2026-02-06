package com.medinsights.patient_service.repositories;

import com.medinsights.patient_service.entities.MedicalAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository for MedicalAnalysis entity
 * Supports US-1.3: Medical History & Follow-up
 */
@Repository
public interface MedicalAnalysisRepository extends JpaRepository<MedicalAnalysis, UUID> {

    /**
     * Find all medical analyses for a specific patient
     */
    List<MedicalAnalysis> findByPatientIdOrderByAnalysisDateDesc(UUID patientId);

    /**
     * Find analyses by type for a patient
     */
    List<MedicalAnalysis> findByPatientIdAndAnalysisTypeOrderByAnalysisDateDesc(UUID patientId, String analysisType);

    /**
     * Find analyses by status
     */
    List<MedicalAnalysis> findByStatusOrderByAnalysisDateDesc(String status);

    /**
     * Find analyses within a date range
     */
    @Query("SELECT m FROM MedicalAnalysis m WHERE m.patient.id = :patientId AND m.analysisDate BETWEEN :startDate AND :endDate ORDER BY m.analysisDate DESC")
    List<MedicalAnalysis> findByPatientIdAndDateRange(
            @Param("patientId") UUID patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Find analyses with alerts or anomalies
     */
    @Query("SELECT m FROM MedicalAnalysis m WHERE m.patient.id = :patientId AND m.alertsAndAnomalies IS NOT NULL AND m.alertsAndAnomalies != '' ORDER BY m.analysisDate DESC")
    List<MedicalAnalysis> findWithAlertsByPatientId(@Param("patientId") UUID patientId);

    /**
     * Count analyses for a patient
     */
    long countByPatientId(UUID patientId);

    /**
     * Find analyses by patient and created by user
     */
    List<MedicalAnalysis> findByPatientIdAndCreatedByOrderByAnalysisDateDesc(UUID patientId, UUID createdBy);
}
