package com.medinsights.patient_service.repositories;

import com.medinsights.patient_service.entities.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Consultation entity
 * Provides database access for consultation records
 */
@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, UUID> {

    /**
     * Find all consultations for a specific patient
     */
    List<Consultation> findByPatientIdOrderByConsultationDateDesc(UUID patientId);

    /**
     * Find consultations by patient and status
     */
    List<Consultation> findByPatientIdAndStatusOrderByConsultationDateDesc(UUID patientId, String status);

    /**
     * Find consultations in date range for a patient
     */
    @Query("SELECT c FROM Consultation c WHERE c.patient.id = :patientId " +
            "AND c.consultationDate BETWEEN :startDate AND :endDate " +
            "ORDER BY c.consultationDate DESC")
    List<Consultation> findByPatientIdAndDateRange(
            @Param("patientId") UUID patientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Count consultations for a patient
     */
    long countByPatientId(UUID patientId);

    /**
     * Count consultations by status for a patient
     */
    long countByPatientIdAndStatus(UUID patientId, String status);

    /**
     * Find most recent consultation for a patient
     */
    @Query("SELECT c FROM Consultation c WHERE c.patient.id = :patientId " +
            "ORDER BY c.consultationDate DESC LIMIT 1")
    Consultation findLatestByPatientId(@Param("patientId") UUID patientId);
}
