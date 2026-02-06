package com.medinsights.patient_service.controllers;

import com.medinsights.patient_service.dto.analysis.MedicalAnalysisCreateRequest;
import com.medinsights.patient_service.dto.analysis.MedicalAnalysisResponse;
import com.medinsights.patient_service.dto.analysis.MedicalAnalysisUpdateRequest;
import com.medinsights.patient_service.services.MedicalAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Medical Analysis management
 * Supports US-1.3, US-1.4: Medical History, Lab Results & Report Generation
 */
@RestController
@RequestMapping("/api/medical-analyses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Medical Analysis Management", description = "APIs for managing lab results and medical analyses (US-1.3, US-1.4)")
public class MedicalAnalysisController {

    private final MedicalAnalysisService medicalAnalysisService;

    @PostMapping("/patients/{patientId}")
    @Operation(
            summary = "Create a new medical analysis",
            description = "Record a new medical analysis/lab result for a patient. Doctor only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Medical analysis created successfully",
                    content = @Content(schema = @Schema(implementation = MedicalAnalysisResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<MedicalAnalysisResponse> createMedicalAnalysis(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Valid @RequestBody MedicalAnalysisCreateRequest request,
            @RequestAttribute("userId") UUID userId
    ) {
        log.info("POST /api/medical-analyses/patients/{} - Creating medical analysis", patientId);
        MedicalAnalysisResponse response = medicalAnalysisService.create(patientId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{analysisId}")
    @Operation(
            summary = "Update a medical analysis",
            description = "Update an existing medical analysis record (e.g., add interpretation, update status). Doctor only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medical analysis updated successfully",
                    content = @Content(schema = @Schema(implementation = MedicalAnalysisResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Medical analysis not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<MedicalAnalysisResponse> updateMedicalAnalysis(
            @Parameter(description = "Medical Analysis UUID") @PathVariable UUID analysisId,
            @Valid @RequestBody MedicalAnalysisUpdateRequest request,
            @RequestAttribute("userId") UUID userId
    ) {
        log.info("PUT /api/medical-analyses/{} - Updating medical analysis", analysisId);
        MedicalAnalysisResponse response = medicalAnalysisService.update(analysisId, request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{analysisId}")
    @Operation(
            summary = "Get medical analysis by ID",
            description = "Retrieve detailed information about a specific medical analysis"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medical analysis found",
                    content = @Content(schema = @Schema(implementation = MedicalAnalysisResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Medical analysis not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<MedicalAnalysisResponse> getMedicalAnalysisById(
            @Parameter(description = "Medical Analysis UUID") @PathVariable UUID analysisId
    ) {
        log.info("GET /api/medical-analyses/{} - Fetching medical analysis", analysisId);
        MedicalAnalysisResponse response = medicalAnalysisService.findById(analysisId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients/{patientId}")
    @Operation(
            summary = "Get all medical analyses for a patient",
            description = "Retrieve all medical analysis records for a specific patient, ordered by analysis date (most recent first)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medical analyses retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MedicalAnalysisResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<MedicalAnalysisResponse>> getPatientMedicalAnalyses(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        log.info("GET /api/medical-analyses/patients/{} - Fetching all analyses", patientId);
        List<MedicalAnalysisResponse> analyses = medicalAnalysisService.findByPatientId(patientId);
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/patients/{patientId}/alerts")
    @Operation(
            summary = "Get analyses with alerts for a patient",
            description = "Retrieve only medical analyses that have alerts or anomalies for a specific patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Analyses with alerts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MedicalAnalysisResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<MedicalAnalysisResponse>> getAnalysesWithAlerts(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        log.info("GET /api/medical-analyses/patients/{}/alerts - Fetching analyses with alerts", patientId);
        List<MedicalAnalysisResponse> analyses = medicalAnalysisService.findWithAlertsByPatientId(patientId);
        return ResponseEntity.ok(analyses);
    }

    @GetMapping("/patients/{patientId}/date-range")
    @Operation(
            summary = "Get medical analyses by date range",
            description = "Retrieve medical analyses for a patient within a specific date range"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Medical analyses retrieved successfully",
                    content = @Content(schema = @Schema(implementation = MedicalAnalysisResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<MedicalAnalysisResponse>> getAnalysesByDateRange(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Parameter(description = "Start date (ISO format: yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (ISO format: yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("GET /api/medical-analyses/patients/{}/date-range - Start: {}, End: {}", patientId, startDate, endDate);
        List<MedicalAnalysisResponse> analyses = medicalAnalysisService.findByPatientIdAndDateRange(patientId, startDate, endDate);
        return ResponseEntity.ok(analyses);
    }

    @DeleteMapping("/{analysisId}")
    @Operation(
            summary = "Delete a medical analysis",
            description = "Delete a medical analysis record. Doctor only. Use with caution - this is a hard delete."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Medical analysis deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Medical analysis not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteMedicalAnalysis(
            @Parameter(description = "Medical Analysis UUID") @PathVariable UUID analysisId
    ) {
        log.info("DELETE /api/medical-analyses/{} - Deleting medical analysis", analysisId);
        medicalAnalysisService.delete(analysisId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patients/{patientId}/count")
    @Operation(
            summary = "Count medical analyses for a patient",
            description = "Get the total number of medical analysis records for a patient"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Long> countMedicalAnalyses(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        log.info("GET /api/medical-analyses/patients/{}/count", patientId);
        long count = medicalAnalysisService.countByPatientId(patientId);
        return ResponseEntity.ok(count);
    }
}
