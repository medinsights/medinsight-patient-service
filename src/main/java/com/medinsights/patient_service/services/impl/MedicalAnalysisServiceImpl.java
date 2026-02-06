package com.medinsights.patient_service.services.impl;

import com.medinsights.patient_service.dto.analysis.MedicalAnalysisCreateRequest;
import com.medinsights.patient_service.dto.analysis.MedicalAnalysisResponse;
import com.medinsights.patient_service.dto.analysis.MedicalAnalysisUpdateRequest;
import com.medinsights.patient_service.entities.MedicalAnalysis;
import com.medinsights.patient_service.entities.Patient;
import com.medinsights.patient_service.repositories.MedicalAnalysisRepository;
import com.medinsights.patient_service.repositories.PatientRepository;
import com.medinsights.patient_service.services.MedicalAnalysisService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of MedicalAnalysisService
 * Supports US-1.3, US-1.4: Medical History, Lab Results & Report Generation
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MedicalAnalysisServiceImpl implements MedicalAnalysisService {

    private final MedicalAnalysisRepository medicalAnalysisRepository;
    private final PatientRepository patientRepository;

    @Override
    public MedicalAnalysisResponse create(UUID patientId, MedicalAnalysisCreateRequest request, UUID userId) {
        log.info("Creating medical analysis for patient: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));

        MedicalAnalysis analysis = new MedicalAnalysis();
        analysis.setPatient(patient);
        analysis.setAnalysisType(request.analysisType());
        analysis.setAnalysisDate(request.analysisDate());
        analysis.setFileName(request.fileName());
        analysis.setOcrText(request.ocrText());
        analysis.setResults(request.results());
        analysis.setInterpretation(request.interpretation());
        analysis.setAlertsAndAnomalies(request.alertsAndAnomalies());
        analysis.setRecommendations(request.recommendations());
        analysis.setPerformedBy(request.performedBy());
        analysis.setInterpretedBy(request.interpretedBy());
        analysis.setStatus(request.status());
        analysis.setNotes(request.notes());
        analysis.setCreatedBy(userId);
        analysis.setUpdatedBy(userId);

        MedicalAnalysis saved = medicalAnalysisRepository.save(analysis);
        log.info("Medical analysis created successfully with id: {}", saved.getId());

        return toResponse(saved);
    }

    @Override
    public MedicalAnalysisResponse update(UUID analysisId, MedicalAnalysisUpdateRequest request, UUID userId) {
        log.info("Updating medical analysis: {}", analysisId);

        MedicalAnalysis analysis = medicalAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new EntityNotFoundException("Medical analysis not found with id: " + analysisId));

        if (request.ocrText() != null) {
            analysis.setOcrText(request.ocrText());
        }
        if (request.results() != null) {
            analysis.setResults(request.results());
        }
        if (request.interpretation() != null) {
            analysis.setInterpretation(request.interpretation());
        }
        if (request.alertsAndAnomalies() != null) {
            analysis.setAlertsAndAnomalies(request.alertsAndAnomalies());
        }
        if (request.recommendations() != null) {
            analysis.setRecommendations(request.recommendations());
        }
        if (request.interpretedBy() != null) {
            analysis.setInterpretedBy(request.interpretedBy());
        }
        if (request.status() != null) {
            analysis.setStatus(request.status());
        }
        if (request.notes() != null) {
            analysis.setNotes(request.notes());
        }

        analysis.setUpdatedBy(userId);

        MedicalAnalysis updated = medicalAnalysisRepository.save(analysis);
        log.info("Medical analysis updated successfully: {}", analysisId);

        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalAnalysisResponse findById(UUID analysisId) {
        log.debug("Finding medical analysis: {}", analysisId);

        MedicalAnalysis analysis = medicalAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new EntityNotFoundException("Medical analysis not found with id: " + analysisId));

        return toResponse(analysis);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalAnalysisResponse> findByPatientId(UUID patientId) {
        log.debug("Finding all medical analyses for patient: {}", patientId);

        return medicalAnalysisRepository.findByPatientIdOrderByAnalysisDateDesc(patientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalAnalysisResponse> findWithAlertsByPatientId(UUID patientId) {
        log.debug("Finding medical analyses with alerts for patient: {}", patientId);

        return medicalAnalysisRepository.findWithAlertsByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalAnalysisResponse> findByPatientIdAndDateRange(UUID patientId, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding medical analyses for patient: {} between {} and {}", patientId, startDate, endDate);

        return medicalAnalysisRepository.findByPatientIdAndDateRange(patientId, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID analysisId) {
        log.info("Deleting medical analysis: {}", analysisId);

        if (!medicalAnalysisRepository.existsById(analysisId)) {
            throw new EntityNotFoundException("Medical analysis not found with id: " + analysisId);
        }

        medicalAnalysisRepository.deleteById(analysisId);
        log.info("Medical analysis deleted successfully: {}", analysisId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByPatientId(UUID patientId) {
        return medicalAnalysisRepository.countByPatientId(patientId);
    }

    /**
     * Convert MedicalAnalysis entity to MedicalAnalysisResponse DTO
     */
    private MedicalAnalysisResponse toResponse(MedicalAnalysis analysis) {
        return new MedicalAnalysisResponse(
                analysis.getId(),
                analysis.getPatient().getId(),
                analysis.getAnalysisType(),
                analysis.getAnalysisDate(),
                analysis.getFileName(),
                analysis.getOcrText(),
                analysis.getResults(),
                analysis.getInterpretation(),
                analysis.getAlertsAndAnomalies(),
                analysis.getRecommendations(),
                analysis.getPerformedBy(),
                analysis.getInterpretedBy(),
                analysis.getStatus(),
                analysis.getNotes(),
                analysis.getCreatedAt(),
                analysis.getUpdatedAt(),
                analysis.getCreatedBy(),
                analysis.getUpdatedBy()
        );
    }
}
