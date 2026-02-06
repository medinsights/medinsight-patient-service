package com.medinsights.patient_service.services.impl;

import com.medinsights.patient_service.dto.treatment.TreatmentCreateRequest;
import com.medinsights.patient_service.dto.treatment.TreatmentResponse;
import com.medinsights.patient_service.dto.treatment.TreatmentUpdateRequest;
import com.medinsights.patient_service.entities.Patient;
import com.medinsights.patient_service.entities.Treatment;
import com.medinsights.patient_service.repositories.PatientRepository;
import com.medinsights.patient_service.repositories.TreatmentRepository;
import com.medinsights.patient_service.services.TreatmentService;
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
 * Implementation of TreatmentService
 * Supports US-1.3: Medical History & Follow-up Management
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final PatientRepository patientRepository;

    @Override
    public TreatmentResponse create(UUID patientId, TreatmentCreateRequest request, UUID userId) {
        log.info("Creating treatment for patient: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));

        Treatment treatment = new Treatment();
        treatment.setPatient(patient);
        treatment.setMedicationName(request.medicationName());
        treatment.setDosage(request.dosage());
        treatment.setFrequency(request.frequency());
        treatment.setRouteOfAdministration(request.routeOfAdministration());
        treatment.setStartDate(request.startDate());
        treatment.setEndDate(request.endDate());
        treatment.setDurationDays(request.durationDays());
        treatment.setStatus(request.status());
        treatment.setNotes(request.notes());
        treatment.setCreatedBy(userId);
        treatment.setUpdatedBy(userId);

        Treatment saved = treatmentRepository.save(treatment);
        log.info("Treatment created successfully with id: {}", saved.getId());

        return toResponse(saved);
    }

    @Override
    public TreatmentResponse update(UUID treatmentId, TreatmentUpdateRequest request, UUID userId) {
        log.info("Updating treatment: {}", treatmentId);

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new EntityNotFoundException("Treatment not found with id: " + treatmentId));

        if (request.medicationName() != null) {
            treatment.setMedicationName(request.medicationName());
        }
        if (request.dosage() != null) {
            treatment.setDosage(request.dosage());
        }
        if (request.frequency() != null) {
            treatment.setFrequency(request.frequency());
        }
        if (request.routeOfAdministration() != null) {
            treatment.setRouteOfAdministration(request.routeOfAdministration());
        }
        if (request.endDate() != null) {
            treatment.setEndDate(request.endDate());
        }
        if (request.durationDays() != null) {
            treatment.setDurationDays(request.durationDays());
        }
        if (request.status() != null) {
            treatment.setStatus(request.status());
        }
        if (request.notes() != null) {
            treatment.setNotes(request.notes());
        }

        treatment.setUpdatedBy(userId);

        Treatment updated = treatmentRepository.save(treatment);
        log.info("Treatment updated successfully: {}", treatmentId);

        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentResponse findById(UUID treatmentId) {
        log.debug("Finding treatment: {}", treatmentId);

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new EntityNotFoundException("Treatment not found with id: " + treatmentId));

        return toResponse(treatment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponse> findByPatientId(UUID patientId) {
        log.debug("Finding all treatments for patient: {}", patientId);

        return treatmentRepository.findByPatientIdOrderByStartDateDesc(patientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponse> findActiveByPatientId(UUID patientId) {
        log.debug("Finding active treatments for patient: {}", patientId);

        return treatmentRepository.findActiveByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponse> findByPatientIdAndDateRange(UUID patientId, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding treatments for patient: {} between {} and {}", patientId, startDate, endDate);

        return treatmentRepository.findByPatientIdAndDateRange(patientId, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID treatmentId) {
        log.info("Deleting treatment: {}", treatmentId);

        if (!treatmentRepository.existsById(treatmentId)) {
            throw new EntityNotFoundException("Treatment not found with id: " + treatmentId);
        }

        treatmentRepository.deleteById(treatmentId);
        log.info("Treatment deleted successfully: {}", treatmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByPatientId(UUID patientId) {
        return treatmentRepository.countByPatientId(patientId);
    }

    /**
     * Convert Treatment entity to TreatmentResponse DTO
     */
    private TreatmentResponse toResponse(Treatment treatment) {
        return new TreatmentResponse(
                treatment.getId(),
                treatment.getPatient().getId(),
                treatment.getMedicationName(),
                treatment.getDosage(),
                treatment.getFrequency(),
                treatment.getRouteOfAdministration(),
                treatment.getStartDate(),
                treatment.getEndDate(),
                treatment.getDurationDays(),
                treatment.getStatus(),
                treatment.getIndication(),
                treatment.getSideEffects(),
                treatment.getPrescriberName(),
                treatment.getNotes(),
                treatment.getCreatedAt(),
                treatment.getUpdatedAt(),
                treatment.getCreatedBy(),
                treatment.getUpdatedBy()
        );
    }
}
