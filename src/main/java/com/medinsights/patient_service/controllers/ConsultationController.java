package com.medinsights.patient_service.controllers;

import com.medinsights.patient_service.dto.consultation.ConsultationCreateRequest;
import com.medinsights.patient_service.dto.consultation.ConsultationResponse;
import com.medinsights.patient_service.dto.consultation.ConsultationUpdateRequest;
import com.medinsights.patient_service.services.impl.ConsultationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing consultations
 * Provides endpoints for consultation CRUD operations
 * Matches chatbot schema for compatibility
 */
@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
@Tag(name = "Consultation Management", description = "APIs for managing patient consultations")
public class ConsultationController {

    private final ConsultationServiceImpl consultationService;

    /**
     * Create a new consultation for a patient
     * Chatbot compatible: POST /api/consultations
     */
    @PostMapping("/patients/{patientId}")
    @Operation(
            summary = "Create consultation",
            description = "Create a new consultation record for a patient. Matches chatbot schema: date_consultation, motif, examen_clinique, diagnostic, prescription, remarques, prochain_rdv"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consultation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<ConsultationResponse> createConsultation(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Valid @RequestBody ConsultationCreateRequest request,
            @Parameter(description = "User ID from JWT") @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ) {
        UUID createdBy = userId != null ? userId : UUID.randomUUID(); // Fallback for testing
        ConsultationResponse response = consultationService.create(patientId, request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing consultation
     */
    @PutMapping("/{consultationId}")
    @Operation(
            summary = "Update consultation",
            description = "Update an existing consultation record. All fields are optional."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultation updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Consultation not found")
    })
    public ResponseEntity<ConsultationResponse> updateConsultation(
            @Parameter(description = "Consultation UUID") @PathVariable UUID consultationId,
            @Valid @RequestBody ConsultationUpdateRequest request,
            @Parameter(description = "User ID from JWT") @RequestHeader(value = "X-User-Id", required = false) UUID userId
    ) {
        UUID updatedBy = userId != null ? userId : UUID.randomUUID();
        ConsultationResponse response = consultationService.update(consultationId, request, updatedBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Get consultation by ID
     */
    @GetMapping("/{consultationId}")
    @Operation(
            summary = "Get consultation by ID",
            description = "Retrieve a specific consultation by its UUID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultation found"),
            @ApiResponse(responseCode = "404", description = "Consultation not found")
    })
    public ResponseEntity<ConsultationResponse> getConsultation(
            @Parameter(description = "Consultation UUID") @PathVariable UUID consultationId
    ) {
        ConsultationResponse response = consultationService.getById(consultationId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all consultations for a patient
     * Chatbot compatible: GET /api/patients/{patient_id}/consultations
     */
    @GetMapping("/patients/{patientId}")
    @Operation(
            summary = "Get patient consultations",
            description = "Retrieve all consultations for a specific patient, ordered by date (newest first)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<List<ConsultationResponse>> getPatientConsultations(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Parameter(description = "Filter by status (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)") 
            @RequestParam(required = false) String status
    ) {
        List<ConsultationResponse> consultations;
        if (status != null) {
            consultations = consultationService.getByPatientIdAndStatus(patientId, status);
        } else {
            consultations = consultationService.getByPatientId(patientId);
        }
        return ResponseEntity.ok(consultations);
    }

    /**
     * Get latest consultation for a patient
     */
    @GetMapping("/patients/{patientId}/latest")
    @Operation(
            summary = "Get latest consultation",
            description = "Retrieve the most recent consultation for a patient"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Latest consultation found"),
            @ApiResponse(responseCode = "404", description = "No consultations found for patient")
    })
    public ResponseEntity<ConsultationResponse> getLatestConsultation(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        ConsultationResponse response = consultationService.getLatestByPatientId(patientId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get consultations in date range
     */
    @GetMapping("/patients/{patientId}/date-range")
    @Operation(
            summary = "Get consultations in date range",
            description = "Retrieve consultations for a patient within a specific date range"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consultations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<List<ConsultationResponse>> getConsultationsInDateRange(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Parameter(description = "Start date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<ConsultationResponse> consultations = consultationService.getByPatientIdAndDateRange(patientId, startDate, endDate);
        return ResponseEntity.ok(consultations);
    }

    /**
     * Delete a consultation
     */
    @DeleteMapping("/{consultationId}")
    @Operation(
            summary = "Delete consultation",
            description = "Permanently delete a consultation record"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Consultation deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Consultation not found")
    })
    public ResponseEntity<Void> deleteConsultation(
            @Parameter(description = "Consultation UUID") @PathVariable UUID consultationId
    ) {
        consultationService.delete(consultationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Count consultations for a patient
     */
    @GetMapping("/patients/{patientId}/count")
    @Operation(
            summary = "Count patient consultations",
            description = "Get the total number of consultations for a patient"
    )
    @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    public ResponseEntity<Long> countConsultations(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        long count = consultationService.countByPatientId(patientId);
        return ResponseEntity.ok(count);
    }
}
