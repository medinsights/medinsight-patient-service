package com.medinsights.patient_service.services;

import com.medinsights.patient_service.dto.CreateMedicalAlertDTO;
import com.medinsights.patient_service.dto.MedicalAlertDTO;
import com.medinsights.patient_service.entities.MedicalAlert;
import com.medinsights.patient_service.entities.Patient;
import com.medinsights.patient_service.exceptions.ResourceNotFoundException;
import com.medinsights.patient_service.repositories.MedicalAlertRepository;
import com.medinsights.patient_service.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing medical alerts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalAlertService {
    
    private final MedicalAlertRepository alertRepository;
    private final PatientRepository patientRepository;
    
    /**
     * Get all alerts for a patient
     */
    @Transactional(readOnly = true)
    public List<MedicalAlertDTO> getPatientAlerts(UUID patientId) {
        log.debug("Fetching alerts for patient: {}", patientId);
        return alertRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active alerts for a patient
     */
    @Transactional(readOnly = true)
    public List<MedicalAlertDTO> getActiveAlerts(UUID patientId) {
        log.debug("Fetching active alerts for patient: {}", patientId);
        return alertRepository.findByPatientIdAndStatus(patientId, "active")
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get alerts by severity
     */
    @Transactional(readOnly = true)
    public List<MedicalAlertDTO> getAlertsBySeverity(UUID patientId, String severity) {
        log.debug("Fetching {} severity alerts for patient: {}", severity, patientId);
        return alertRepository.findByPatientIdAndSeverityLevel(patientId, severity)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Create a new alert
     */
    @Transactional
    public MedicalAlertDTO createAlert(CreateMedicalAlertDTO dto, UUID createdBy) {
        log.info("Creating alert for patient: {}", dto.getPatientId());
        
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));
        
        MedicalAlert alert = new MedicalAlert();
        alert.setPatient(patient);
        alert.setAlertType(dto.getAlertType());
        alert.setSeverityLevel(dto.getSeverityLevel());
        alert.setDescription(dto.getDescription());
        alert.setRequiredAction(dto.getRequiredAction());
        alert.setStatus("active");
        alert.setCreatedBy(createdBy);
        
        MedicalAlert savedAlert = alertRepository.save(alert);
        log.info("Alert created successfully with id: {}", savedAlert.getId());
        
        return toDTO(savedAlert);
    }
    
    /**
     * Resolve an alert
     */
    @Transactional
    public MedicalAlertDTO resolveAlert(UUID alertId, UUID resolvedBy) {
        log.info("Resolving alert: {}", alertId);
        
        MedicalAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + alertId));
        
        alert.setStatus("resolved");
        alert.setResolutionDate(Instant.now());
        alert.setResolvedBy(resolvedBy);
        
        MedicalAlert savedAlert = alertRepository.save(alert);
        log.info("Alert resolved successfully: {}", alertId);
        
        return toDTO(savedAlert);
    }
    
    /**
     * Dismiss an alert (not resolved, just dismissed)
     */
    @Transactional
    public MedicalAlertDTO dismissAlert(UUID alertId, UUID dismissedBy) {
        log.info("Dismissing alert: {}", alertId);
        
        MedicalAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with id: " + alertId));
        
        alert.setStatus("dismissed");
        alert.setResolvedBy(dismissedBy);
        
        MedicalAlert savedAlert = alertRepository.save(alert);
        log.info("Alert dismissed successfully: {}", alertId);
        
        return toDTO(savedAlert);
    }
    
    /**
     * Delete an alert
     */
    @Transactional
    public void deleteAlert(UUID alertId) {
        log.info("Deleting alert: {}", alertId);
        
        if (!alertRepository.existsById(alertId)) {
            throw new ResourceNotFoundException("Alert not found with id: " + alertId);
        }
        
        alertRepository.deleteById(alertId);
        log.info("Alert deleted successfully: {}", alertId);
    }
    
    /**
     * Count active alerts for a patient
     */
    @Transactional(readOnly = true)
    public long countActiveAlerts(UUID patientId) {
        return alertRepository.countByPatientIdAndStatus(patientId, "active");
    }
    
    /**
     * Convert entity to DTO
     */
    private MedicalAlertDTO toDTO(MedicalAlert entity) {
        MedicalAlertDTO dto = new MedicalAlertDTO();
        dto.setId(entity.getId());
        dto.setPatientId(entity.getPatient().getId());
        dto.setAlertType(entity.getAlertType());
        dto.setSeverityLevel(entity.getSeverityLevel());
        dto.setDescription(entity.getDescription());
        dto.setResolutionDate(entity.getResolutionDate());
        dto.setStatus(entity.getStatus());
        dto.setRequiredAction(entity.getRequiredAction());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setResolvedBy(entity.getResolvedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
