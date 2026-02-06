package com.medinsights.patient_service.services.impl;

import com.medinsights.patient_service.dto.cardiovascular.CardiovascularExamCreateRequest;
import com.medinsights.patient_service.dto.cardiovascular.CardiovascularExamResponse;
import com.medinsights.patient_service.dto.cardiovascular.CardiovascularExamUpdateRequest;
import com.medinsights.patient_service.entities.CardiovascularExam;
import com.medinsights.patient_service.entities.Patient;
import com.medinsights.patient_service.exceptions.ResourceNotFoundException;
import com.medinsights.patient_service.repositories.CardiovascularExamRepository;
import com.medinsights.patient_service.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing cardiovascular examinations
 * Business logic for cardiovascular exam CRUD operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CardiovascularExamServiceImpl {

    private final CardiovascularExamRepository cardiovascularExamRepository;
    private final PatientRepository patientRepository;

    /**
     * Create a new cardiovascular exam for a patient
     */
    public CardiovascularExamResponse create(CardiovascularExamCreateRequest request) {
        // Validate patient exists
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.patientId()));

        // Create exam entity
        CardiovascularExam exam = new CardiovascularExam();
        exam.setPatient(patient);
        exam.setExamType(request.examType());
        exam.setExamDate(request.examDate());
        exam.setResults(request.results());
        exam.setInterpretation(request.interpretation());
        exam.setMeasuredValues(request.measuredValues());
        exam.setAbnormalities(request.abnormalities());
        exam.setPdfFile(request.pdfFile());
        exam.setNotes(request.notes());
        exam.setStatus(request.status() != null ? request.status() : "COMPLETED");

        // Save and return
        CardiovascularExam saved = cardiovascularExamRepository.save(exam);
        return toResponse(saved);
    }

    /**
     * Update an existing cardiovascular exam
     */
    public CardiovascularExamResponse update(UUID examId, CardiovascularExamUpdateRequest request) {
        // Find existing exam
        CardiovascularExam exam = cardiovascularExamRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Cardiovascular exam not found with id: " + examId));

        // Update fields if provided
        if (request.examType() != null) exam.setExamType(request.examType());
        if (request.examDate() != null) exam.setExamDate(request.examDate());
        if (request.results() != null) exam.setResults(request.results());
        if (request.interpretation() != null) exam.setInterpretation(request.interpretation());
        if (request.measuredValues() != null) exam.setMeasuredValues(request.measuredValues());
        if (request.abnormalities() != null) exam.setAbnormalities(request.abnormalities());
        if (request.pdfFile() != null) exam.setPdfFile(request.pdfFile());
        if (request.notes() != null) exam.setNotes(request.notes());
        if (request.status() != null) exam.setStatus(request.status());

        // Save and return
        CardiovascularExam updated = cardiovascularExamRepository.save(exam);
        return toResponse(updated);
    }

    /**
     * Get cardiovascular exam by ID
     */
    @Transactional(readOnly = true)
    public CardiovascularExamResponse getById(UUID examId) {
        CardiovascularExam exam = cardiovascularExamRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Cardiovascular exam not found with id: " + examId));
        return toResponse(exam);
    }

    /**
     * Get all cardiovascular exams for a patient
     */
    @Transactional(readOnly = true)
    public List<CardiovascularExamResponse> getByPatientId(UUID patientId) {
        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        return cardiovascularExamRepository.findByPatientIdOrderByExamDateDesc(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get cardiovascular exams by patient and exam type
     */
    @Transactional(readOnly = true)
    public List<CardiovascularExamResponse> getByPatientIdAndExamType(UUID patientId, String examType) {
        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        return cardiovascularExamRepository.findByPatientIdAndExamTypeOrderByExamDateDesc(patientId, examType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get cardiovascular exams in date range
     */
    @Transactional(readOnly = true)
    public List<CardiovascularExamResponse> getByPatientIdAndDateRange(UUID patientId, LocalDateTime startDate, LocalDateTime endDate) {
        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        return cardiovascularExamRepository.findByPatientIdAndDateRange(patientId, startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get cardiovascular exams with abnormalities
     */
    @Transactional(readOnly = true)
    public List<CardiovascularExamResponse> getByPatientIdWithAbnormalities(UUID patientId) {
        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        return cardiovascularExamRepository.findByPatientIdWithAbnormalities(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Get latest cardiovascular exam for patient
     */
    @Transactional(readOnly = true)
    public CardiovascularExamResponse getLatestByPatientId(UUID patientId) {
        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        CardiovascularExam exam = cardiovascularExamRepository.findLatestByPatientId(patientId);
        if (exam == null) {
            throw new ResourceNotFoundException("No cardiovascular exams found for patient: " + patientId);
        }

        return toResponse(exam);
    }

    /**
     * Delete a cardiovascular exam
     */
    public void delete(UUID examId) {
        if (!cardiovascularExamRepository.existsById(examId)) {
            throw new ResourceNotFoundException("Cardiovascular exam not found with id: " + examId);
        }
        cardiovascularExamRepository.deleteById(examId);
    }

    /**
     * Count cardiovascular exams for a patient
     */
    @Transactional(readOnly = true)
    public long countByPatientId(UUID patientId) {
        return cardiovascularExamRepository.countByPatientId(patientId);
    }

    /**
     * Convert entity to response DTO
     */
    private CardiovascularExamResponse toResponse(CardiovascularExam exam) {
        return new CardiovascularExamResponse(
                exam.getId(),
                exam.getPatient().getId(),
                exam.getExamType(),
                exam.getExamDate(),
                exam.getResults(),
                exam.getInterpretation(),
                exam.getMeasuredValues(),
                exam.getAbnormalities(),
                exam.getPdfFile(),
                exam.getNotes(),
                exam.getStatus(),
                exam.getCreatedAt(),
                exam.getUpdatedAt()
        );
    }
}
