package com.medinsights.patient_service.repositories;

import com.medinsights.patient_service.entities.VitalSigns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for VitalSigns entity
 * Supports US-1.3: Medical History & Follow-up
 */
@Repository
public interface VitalSignsRepository extends JpaRepository<VitalSigns, UUID> {

    /**
     * Find all vital signs for a specific patient
     */
    List<VitalSigns> findByPatientIdOrderByMeasurementDateDesc(UUID patientId);

    /**
     * Find latest vital signs for a patient
     */
    @Query("SELECT v FROM VitalSigns v WHERE v.patient.id = :patientId ORDER BY v.measurementDate DESC LIMIT 1")
    Optional<VitalSigns> findLatestByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find vital signs within a date range
     */
    @Query("SELECT v FROM VitalSigns v WHERE v.patient.id = :patientId AND DATE(v.measurementDate) BETWEEN :startDate AND :endDate ORDER BY v.measurementDate DESC")
    List<VitalSigns> findByPatientIdAndDateRange(
            @Param("patientId") UUID patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Count vital signs records for a patient
     */
    long countByPatientId(UUID patientId);

    /**
     * Find vital signs recorded by a specific user
     */
    List<VitalSigns> findByCreatedByOrderByMeasurementDateDesc(UUID createdBy);
}
