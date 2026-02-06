package com.medinsights.patient_service.controllers;

import com.medinsights.patient_service.dto.cardiovascular.CardiovascularExamCreateRequest;
import com.medinsights.patient_service.dto.cardiovascular.CardiovascularExamResponse;
import com.medinsights.patient_service.dto.cardiovascular.CardiovascularExamUpdateRequest;
import com.medinsights.patient_service.services.impl.CardiovascularExamServiceImpl;
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
 * REST Controller for managing cardiovascular examinations
 * Provides endpoints for cardiovascular exam CRUD operations
 * Matches chatbot examens_cardiovasculaires table
 */
@RestController
@RequestMapping("/api/cardiovascular-exams")
@RequiredArgsConstructor
@Tag(name = "Cardiovascular Exam Management", description = "APIs for managing patient cardiovascular examinations")
public class CardiovascularExamController {

    private final CardiovascularExamServiceImpl cardiovascularExamService;

    /**
     * Create a new cardiovascular exam
     * Chatbot compatible: POST /api/cardiovascular-exams
     */
    @PostMapping
    @Operation(
            summary = "Create cardiovascular exam",
            description = "Create a new cardiovascular examination record. Matches chatbot schema: type_examen, date_examen, resultats, interpretation, valeurs_mesurees, anomalies, fichier_pdf"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cardiovascular exam created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<CardiovascularExamResponse> createExam(
            @Valid @RequestBody CardiovascularExamCreateRequest request
    ) {
        CardiovascularExamResponse response = cardiovascularExamService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing cardiovascular exam
     */
    @PutMapping("/{examId}")
    @Operation(
            summary = "Update cardiovascular exam",
            description = "Update an existing cardiovascular examination record. All fields are optional."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardiovascular exam updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Cardiovascular exam not found")
    })
    public ResponseEntity<CardiovascularExamResponse> updateExam(
            @Parameter(description = "Exam UUID") @PathVariable UUID examId,
            @Valid @RequestBody CardiovascularExamUpdateRequest request
    ) {
        CardiovascularExamResponse response = cardiovascularExamService.update(examId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get cardiovascular exam by ID
     */
    @GetMapping("/{examId}")
    @Operation(
            summary = "Get cardiovascular exam by ID",
            description = "Retrieve a specific cardiovascular examination by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardiovascular exam found"),
            @ApiResponse(responseCode = "404", description = "Cardiovascular exam not found")
    })
    public ResponseEntity<CardiovascularExamResponse> getExamById(
            @Parameter(description = "Exam UUID") @PathVariable UUID examId
    ) {
        CardiovascularExamResponse response = cardiovascularExamService.getById(examId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all cardiovascular exams for a patient
     */
    @GetMapping("/patients/{patientId}")
    @Operation(
            summary = "Get patient cardiovascular exams",
            description = "Retrieve all cardiovascular examinations for a specific patient, ordered by exam date (newest first)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardiovascular exams retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<List<CardiovascularExamResponse>> getExamsByPatientId(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        List<CardiovascularExamResponse> response = cardiovascularExamService.getByPatientId(patientId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get cardiovascular exams by patient and exam type
     */
    @GetMapping("/patients/{patientId}/type/{examType}")
    @Operation(
            summary = "Get patient exams by type",
            description = "Retrieve all cardiovascular examinations for a patient filtered by exam type (e.g., ECG, Echocardiography)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardiovascular exams retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<List<CardiovascularExamResponse>> getExamsByPatientIdAndType(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Parameter(description = "Exam type") @PathVariable String examType
    ) {
        List<CardiovascularExamResponse> response = cardiovascularExamService.getByPatientIdAndExamType(patientId, examType);
        return ResponseEntity.ok(response);
    }

    /**
     * Get cardiovascular exams in date range
     */
    @GetMapping("/patients/{patientId}/date-range")
    @Operation(
            summary = "Get patient exams by date range",
            description = "Retrieve cardiovascular examinations for a patient within a specific date range"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardiovascular exams retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<List<CardiovascularExamResponse>> getExamsByDateRange(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Parameter(description = "Start date (yyyy-MM-dd HH:mm:ss)")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @Parameter(description = "End date (yyyy-MM-dd HH:mm:ss)")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate
    ) {
        List<CardiovascularExamResponse> response = cardiovascularExamService.getByPatientIdAndDateRange(patientId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Get cardiovascular exams with abnormalities
     */
    @GetMapping("/patients/{patientId}/abnormalities")
    @Operation(
            summary = "Get patient exams with abnormalities",
            description = "Retrieve all cardiovascular examinations for a patient that have detected abnormalities"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardiovascular exams with abnormalities retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    public ResponseEntity<List<CardiovascularExamResponse>> getExamsWithAbnormalities(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        List<CardiovascularExamResponse> response = cardiovascularExamService.getByPatientIdWithAbnormalities(patientId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get latest cardiovascular exam for patient
     */
    @GetMapping("/patients/{patientId}/latest")
    @Operation(
            summary = "Get patient's latest exam",
            description = "Retrieve the most recent cardiovascular examination for a patient"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Latest cardiovascular exam found"),
            @ApiResponse(responseCode = "404", description = "Patient not found or no exams exist")
    })
    public ResponseEntity<CardiovascularExamResponse> getLatestExam(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        CardiovascularExamResponse response = cardiovascularExamService.getLatestByPatientId(patientId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a cardiovascular exam
     */
    @DeleteMapping("/{examId}")
    @Operation(
            summary = "Delete cardiovascular exam",
            description = "Delete a cardiovascular examination record by its ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cardiovascular exam deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Cardiovascular exam not found")
    })
    public ResponseEntity<Void> deleteExam(
            @Parameter(description = "Exam UUID") @PathVariable UUID examId
    ) {
        cardiovascularExamService.delete(examId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Count cardiovascular exams for a patient
     */
    @GetMapping("/patients/{patientId}/count")
    @Operation(
            summary = "Count patient exams",
            description = "Get the total number of cardiovascular examinations for a patient"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<Long> countExams(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        long count = cardiovascularExamService.countByPatientId(patientId);
        return ResponseEntity.ok(count);
    }
}
