package com.medinsights.patient_service.repositories;

import com.medinsights.patient_service.entities.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Treatment entity
 * Supports US-1.3: Medical History & Follow-up
 */
@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {

    /**
     * Find all treatments for a specific patient
     */
    List<Treatment> findByPatientIdOrderByStartDateDesc(UUID patientId);

    /**
     * Find active treatments for a patient
     */
    @Query("SELECT t FROM Treatment t WHERE t.patient.id = :patientId AND t.status = 'ACTIVE' ORDER BY t.startDate DESC")
    List<Treatment> findActiveByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find treatments by status
     */
    List<Treatment> findByStatusOrderByStartDateDesc(String status);

    /**
     * Count active treatments for a patient
     */
    @Query("SELECT COUNT(t) FROM Treatment t WHERE t.patient.id = :patientId AND t.status = 'ACTIVE'")
    long countActiveByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find treatments by patient and created by user
     */
    List<Treatment> findByPatientIdAndCreatedByOrderByStartDateDesc(UUID patientId, UUID createdBy);

    /**
     * Find treatments by date range
     */
    @Query("SELECT t FROM Treatment t WHERE t.patient.id = :patientId AND t.startDate BETWEEN :startDate AND :endDate ORDER BY t.startDate DESC")
    List<Treatment> findByPatientIdAndDateRange(
            @Param("patientId") UUID patientId,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );

    /**
     * Count treatments for a patient
     */
    long countByPatientId(UUID patientId);
}
