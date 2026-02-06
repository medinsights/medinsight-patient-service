package com.medinsights.patient_service.repositories;

import com.medinsights.patient_service.entities.CardiovascularExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for CardiovascularExam entity
 * Provides database access for cardiovascular examination records
 */
@Repository
public interface CardiovascularExamRepository extends JpaRepository<CardiovascularExam, UUID> {

    /**
     * Find all cardiovascular exams for a specific patient
     */
    List<CardiovascularExam> findByPatientIdOrderByExamDateDesc(UUID patientId);

    /**
     * Find cardiovascular exams by patient and exam type
     */
    List<CardiovascularExam> findByPatientIdAndExamTypeOrderByExamDateDesc(UUID patientId, String examType);

    /**
     * Find cardiovascular exams by patient and status
     */
    List<CardiovascularExam> findByPatientIdAndStatusOrderByExamDateDesc(UUID patientId, String status);

    /**
     * Find cardiovascular exams in date range for a patient
     */
    @Query("SELECT ce FROM CardiovascularExam ce WHERE ce.patient.id = :patientId " +
            "AND ce.examDate BETWEEN :startDate AND :endDate " +
            "ORDER BY ce.examDate DESC")
    List<CardiovascularExam> findByPatientIdAndDateRange(
            @Param("patientId") UUID patientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find cardiovascular exams with abnormalities for a patient
     */
    @Query("SELECT ce FROM CardiovascularExam ce WHERE ce.patient.id = :patientId " +
            "AND ce.abnormalities IS NOT NULL AND ce.abnormalities <> '' " +
            "ORDER BY ce.examDate DESC")
    List<CardiovascularExam> findByPatientIdWithAbnormalities(@Param("patientId") UUID patientId);

    /**
     * Count cardiovascular exams for a patient
     */
    long countByPatientId(UUID patientId);

    /**
     * Count cardiovascular exams by type for a patient
     */
    long countByPatientIdAndExamType(UUID patientId, String examType);

    /**
     * Find latest cardiovascular exam for a patient
     */
    @Query("SELECT ce FROM CardiovascularExam ce WHERE ce.patient.id = :patientId " +
            "ORDER BY ce.examDate DESC LIMIT 1")
    CardiovascularExam findLatestByPatientId(@Param("patientId") UUID patientId);
}
