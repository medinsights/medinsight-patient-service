package com.medinsights.patient_service.services.impl;

import com.medinsights.patient_service.dto.vitalsigns.VitalSignsCreateRequest;
import com.medinsights.patient_service.dto.vitalsigns.VitalSignsResponse;
import com.medinsights.patient_service.entities.Patient;
import com.medinsights.patient_service.entities.VitalSigns;
import com.medinsights.patient_service.repositories.PatientRepository;
import com.medinsights.patient_service.repositories.VitalSignsRepository;
import com.medinsights.patient_service.services.VitalSignsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of VitalSignsService
 * Supports US-1.3: Medical History & Follow-up Management
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VitalSignsServiceImpl implements VitalSignsService {

    private final VitalSignsRepository vitalSignsRepository;
    private final PatientRepository patientRepository;

    @Override
    public VitalSignsResponse create(UUID patientId, VitalSignsCreateRequest request, UUID userId) {
        log.info("Recording vital signs for patient: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + patientId));

        VitalSigns vitalSigns = new VitalSigns();
        vitalSigns.setPatient(patient);
        vitalSigns.setMeasurementDate(request.measurementDate());
        vitalSigns.setSystolicBP(request.systolicBP());
        vitalSigns.setDiastolicBP(request.diastolicBP());
        vitalSigns.setHeartRate(request.heartRate());
        vitalSigns.setTemperature(request.temperature());
        vitalSigns.setWeight(request.weight());
        vitalSigns.setHeight(request.height());
        vitalSigns.setRespiratoryRate(request.respiratoryRate());
        vitalSigns.setOxygenSaturation(request.oxygenSaturation());
        vitalSigns.setBloodGlucose(request.bloodGlucose());
        vitalSigns.setNotes(request.notes());
        vitalSigns.setCreatedBy(userId);
        vitalSigns.setUpdatedBy(userId);

        // BMI is automatically calculated via @PrePersist
        VitalSigns saved = vitalSignsRepository.save(vitalSigns);
        log.info("Vital signs recorded successfully with id: {}", saved.getId());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VitalSignsResponse findById(UUID vitalSignsId) {
        log.debug("Finding vital signs: {}", vitalSignsId);

        VitalSigns vitalSigns = vitalSignsRepository.findById(vitalSignsId)
                .orElseThrow(() -> new EntityNotFoundException("Vital signs not found with id: " + vitalSignsId));

        return toResponse(vitalSigns);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalSignsResponse> findByPatientId(UUID patientId) {
        log.debug("Finding all vital signs for patient: {}", patientId);

        return vitalSignsRepository.findByPatientIdOrderByMeasurementDateDesc(patientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VitalSignsResponse> findLatestByPatientId(UUID patientId) {
        log.debug("Finding latest vital signs for patient: {}", patientId);

        return vitalSignsRepository.findLatestByPatientId(patientId)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VitalSignsResponse> findByPatientIdAndDateRange(UUID patientId, LocalDate startDate, LocalDate endDate) {
        log.debug("Finding vital signs for patient: {} between {} and {}", patientId, startDate, endDate);

        return vitalSignsRepository.findByPatientIdAndDateRange(patientId, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID vitalSignsId) {
        log.info("Deleting vital signs: {}", vitalSignsId);

        if (!vitalSignsRepository.existsById(vitalSignsId)) {
            throw new EntityNotFoundException("Vital signs not found with id: " + vitalSignsId);
        }

        vitalSignsRepository.deleteById(vitalSignsId);
        log.info("Vital signs deleted successfully: {}", vitalSignsId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByPatientId(UUID patientId) {
        return vitalSignsRepository.countByPatientId(patientId);
    }

    /**
     * Convert VitalSigns entity to VitalSignsResponse DTO
     */
    private VitalSignsResponse toResponse(VitalSigns vitalSigns) {
        return new VitalSignsResponse(
                vitalSigns.getId(),
                vitalSigns.getPatient().getId(),
                vitalSigns.getMeasurementDate(),
                vitalSigns.getSystolicBP(),
                vitalSigns.getDiastolicBP(),
                vitalSigns.getHeartRate(),
                vitalSigns.getTemperature(),
                vitalSigns.getWeight(),
                vitalSigns.getHeight(),
                vitalSigns.getBmi(),
                vitalSigns.getRespiratoryRate(),
                vitalSigns.getOxygenSaturation(),
                vitalSigns.getBloodGlucose(),
                vitalSigns.getNotes(),
                vitalSigns.getCreatedAt(),
                vitalSigns.getUpdatedAt(),
                vitalSigns.getCreatedBy(),
                vitalSigns.getUpdatedBy()
        );
    }
}
