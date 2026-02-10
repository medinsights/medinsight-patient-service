package com.medinsights.patient_service.repositories;

import com.medinsights.patient_service.entities.MedicalAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for MedicalAlert entity
 */
@Repository
public interface MedicalAlertRepository extends JpaRepository<MedicalAlert, UUID> {
    
    /**
     * Find all alerts for a patient ordered by creation date (newest first)
     */
    List<MedicalAlert> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
    
    /**
     * Find alerts by patient and status
     */
    List<MedicalAlert> findByPatientIdAndStatus(UUID patientId, String status);
    
    /**
     * Find all alerts by status
     */
    List<MedicalAlert> findByStatusOrderByCreatedAtDesc(String status);
    
    /**
     * Find alerts by severity level
     */
    List<MedicalAlert> findBySeverityLevelOrderByCreatedAtDesc(String severityLevel);
    
    /**
     * Count active alerts for a patient
     */
    long countByPatientIdAndStatus(UUID patientId, String status);
    
    /**
     * Find alerts by patient and severity
     */
    List<MedicalAlert> findByPatientIdAndSeverityLevel(UUID patientId, String severityLevel);
}
