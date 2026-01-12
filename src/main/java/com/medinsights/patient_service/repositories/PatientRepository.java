package com.medinsights.patient_service.repositories;

import com.medinsights.patient_service.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    List<Patient> findByCreatedBy(UUID userId);

    List<Patient> findByActiveTrue();

    List<Patient> findByCreatedByAndActiveTrue(UUID userId);

    Optional<Patient> findByEmail(String email);

    @Query("SELECT p FROM Patient p WHERE " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "p.createdBy = :userId")
    List<Patient> searchPatients(@Param("search") String search, @Param("userId") UUID userId);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.createdBy = :userId AND p.active = true")
    long countActivePatientsByUser(@Param("userId") UUID userId);
}
