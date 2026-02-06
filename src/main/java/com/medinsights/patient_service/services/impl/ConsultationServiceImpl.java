package com.medinsights.patient_service.services.impl;

import com.medinsights.patient_service.dto.consultation.ConsultationCreateRequest;
import com.medinsights.patient_service.dto.consultation.ConsultationResponse;
import com.medinsights.patient_service.dto.consultation.ConsultationUpdateRequest;
import com.medinsights.patient_service.entities.Consultation;
import com.medinsights.patient_service.entities.Patient;
import com.medinsights.patient_service.exceptions.ResourceNotFoundException;
import com.medinsights.patient_service.repositories.ConsultationRepository;
import com.medinsights.patient_service.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing consultations
 * Business logic for consultation CRUD operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ConsultationServiceImpl {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;

    /**
     * Create a new consultation for a patient
     */
    public ConsultationResponse create(UUID patientId, ConsultationCreateRequest request, UUID createdBy) {
        // Validate patient exists
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        // Create consultation entity
        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setConsultationDate(request.consultationDate());
        consultation.setReasonForVisit(request.reasonForVisit());
        consultation.setSymptoms(request.symptoms());
        consultation.setPhysicalExamination(request.physicalExamination());
        consultation.setDiagnosis(request.diagnosis());
        consultation.setTreatment(request.treatment());
        consultation.setPrescriptions(request.prescriptions());
        consultation.setNotes(request.notes());
        consultation.setVitalSigns(request.vitalSigns());
        consultation.setFollowUpInstructions(request.followUpInstructions());
        consultation.setNextAppointment(request.nextAppointment());
        consultation.setStatus(request.status() != null ? request.status() : "COMPLETED");
        consultation.setCreatedBy(createdBy);

        // Save and return
        Consultation saved = consultationRepository.save(consultation);
        return toResponse(saved);
    }

    /**
     * Update an existing consultation
     */
    public ConsultationResponse update(UUID consultationId, ConsultationUpdateRequest request, UUID updatedBy) {
        // Find existing consultation
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id: " + consultationId));

        // Update fields if provided
        if (request.consultationDate() != null) consultation.setConsultationDate(request.consultationDate());
        if (request.reasonForVisit() != null) consultation.setReasonForVisit(request.reasonForVisit());
        if (request.symptoms() != null) consultation.setSymptoms(request.symptoms());
        if (request.physicalExamination() != null) consultation.setPhysicalExamination(request.physicalExamination());
        if (request.diagnosis() != null) consultation.setDiagnosis(request.diagnosis());
        if (request.treatment() != null) consultation.setTreatment(request.treatment());
        if (request.prescriptions() != null) consultation.setPrescriptions(request.prescriptions());
        if (request.notes() != null) consultation.setNotes(request.notes());
        if (request.vitalSigns() != null) consultation.setVitalSigns(request.vitalSigns());
        if (request.followUpInstructions() != null) consultation.setFollowUpInstructions(request.followUpInstructions());
        if (request.nextAppointment() != null) consultation.setNextAppointment(request.nextAppointment());
        if (request.status() != null) consultation.setStatus(request.status());
        consultation.setUpdatedBy(updatedBy);

        // Save and return
        Consultation updated = consultationRepository.save(consultation);
        return toResponse(updated);
    }

    /**
     * Get consultation by ID
     */
    @Transactional(readOnly = true)
    public ConsultationResponse getById(UUID consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation not found with id: " + consultationId));
        return toResponse(consultation);
    }

    /**
     * Get all consultations for a patient
     */
    @Transactional(readOnly = true)
    public List<ConsultationResponse> getByPatientId(UUID patientId) {
        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        return consultationRepository.findByPatientIdOrderByConsultationDateDesc(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get consultations by patient and status
     */
    @Transactional(readOnly = true)
    public List<ConsultationResponse> getByPatientIdAndStatus(UUID patientId, String status) {
        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        return consultationRepository.findByPatientIdAndStatusOrderByConsultationDateDesc(patientId, status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get consultations in date range
     */
    @Transactional(readOnly = true)
    public List<ConsultationResponse> getByPatientIdAndDateRange(UUID patientId, LocalDateTime startDate, LocalDateTime endDate) {
        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        return consultationRepository.findByPatientIdAndDateRange(patientId, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Delete a consultation
     */
    public void delete(UUID consultationId) {
        if (!consultationRepository.existsById(consultationId)) {
            throw new ResourceNotFoundException("Consultation not found with id: " + consultationId);
        }
        consultationRepository.deleteById(consultationId);
    }

    /**
     * Count consultations for a patient
     */
    @Transactional(readOnly = true)
    public long countByPatientId(UUID patientId) {
        return consultationRepository.countByPatientId(patientId);
    }

    /**
     * Get latest consultation for a patient
     */
    @Transactional(readOnly = true)
    public ConsultationResponse getLatestByPatientId(UUID patientId) {
        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        Consultation consultation = consultationRepository.findLatestByPatientId(patientId);
        if (consultation == null) {
            throw new ResourceNotFoundException("No consultations found for patient: " + patientId);
        }
        return toResponse(consultation);
    }

    /**
     * Convert entity to response DTO
     */
    private ConsultationResponse toResponse(Consultation consultation) {
        return new ConsultationResponse(
                consultation.getId(),
                consultation.getPatient().getId(),
                consultation.getConsultationDate(),
                consultation.getReasonForVisit(),
                consultation.getSymptoms(),
                consultation.getPhysicalExamination(),
                consultation.getDiagnosis(),
                consultation.getTreatment(),
                consultation.getPrescriptions(),
                consultation.getNotes(),
                consultation.getVitalSigns(),
                consultation.getFollowUpInstructions(),
                consultation.getNextAppointment(),
                consultation.getStatus(),
                consultation.getCreatedAt(),
                consultation.getUpdatedAt(),
                consultation.getCreatedBy(),
                consultation.getUpdatedBy()
        );
    }
}
