package com.medinsights.patient_service.controllers;

import com.medinsights.patient_service.dto.treatment.TreatmentCreateRequest;
import com.medinsights.patient_service.dto.treatment.TreatmentResponse;
import com.medinsights.patient_service.dto.treatment.TreatmentUpdateRequest;
import com.medinsights.patient_service.services.TreatmentService;
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
 * REST Controller for Treatment management
 * Supports US-1.3: Medical History & Follow-up Management
 */
@RestController
@RequestMapping("/api/treatments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Treatment Management", description = "APIs for managing patient treatments and prescriptions (US-1.3)")
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping("/patients/{patientId}")
    @Operation(
            summary = "Create a new treatment",
            description = "Record a new treatment/prescription for a patient. Doctor only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Treatment created successfully",
                    content = @Content(schema = @Schema(implementation = TreatmentResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<TreatmentResponse> createTreatment(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Valid @RequestBody TreatmentCreateRequest request,
            @RequestAttribute("userId") UUID userId
    ) {
        log.info("POST /api/treatments/patients/{} - Creating treatment", patientId);
        TreatmentResponse response = treatmentService.create(patientId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping({"/{treatmentId}", "/traitements/{treatmentId}"})
    @Operation(
            summary = "Update a treatment",
            description = "Update an existing treatment record. Doctor only."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Treatment updated successfully",
                    content = @Content(schema = @Schema(implementation = TreatmentResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Treatment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<TreatmentResponse> updateTreatment(
            @Parameter(description = "Treatment UUID") @PathVariable UUID treatmentId,
            @Valid @RequestBody TreatmentUpdateRequest request,
            @RequestAttribute("userId") UUID userId
    ) {
        log.info("PUT /api/treatments/{} - Updating treatment", treatmentId);
        TreatmentResponse response = treatmentService.update(treatmentId, request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{treatmentId}")
    @Operation(
            summary = "Get treatment by ID",
            description = "Retrieve detailed information about a specific treatment"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Treatment found",
                    content = @Content(schema = @Schema(implementation = TreatmentResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Treatment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<TreatmentResponse> getTreatmentById(
            @Parameter(description = "Treatment UUID") @PathVariable UUID treatmentId
    ) {
        log.info("GET /api/treatments/{} - Fetching treatment", treatmentId);
        TreatmentResponse response = treatmentService.findById(treatmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping({"/patients/{patientId}", "/patients/{patientId}/traitements"})
    @Operation(
            summary = "Get all treatments for a patient",
            description = "Retrieve all treatment records for a specific patient, ordered by start date (most recent first)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Treatments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TreatmentResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TreatmentResponse>> getPatientTreatments(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        log.info("GET /api/treatments/patients/{} - Fetching all treatments", patientId);
        List<TreatmentResponse> treatments = treatmentService.findByPatientId(patientId);
        return ResponseEntity.ok(treatments);
    }

    @GetMapping("/patients/{patientId}/active")
    @Operation(
            summary = "Get active treatments for a patient",
            description = "Retrieve only active treatments for a specific patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Active treatments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TreatmentResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TreatmentResponse>> getActivePatientTreatments(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        log.info("GET /api/treatments/patients/{}/active - Fetching active treatments", patientId);
        List<TreatmentResponse> treatments = treatmentService.findActiveByPatientId(patientId);
        return ResponseEntity.ok(treatments);
    }

    @GetMapping("/patients/{patientId}/date-range")
    @Operation(
            summary = "Get treatments by date range",
            description = "Retrieve treatments for a patient within a specific date range"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Treatments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TreatmentResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TreatmentResponse>> getTreatmentsByDateRange(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Parameter(description = "Start date (ISO format: yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (ISO format: yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("GET /api/treatments/patients/{}/date-range - Start: {}, End: {}", patientId, startDate, endDate);
        List<TreatmentResponse> treatments = treatmentService.findByPatientIdAndDateRange(patientId, startDate, endDate);
        return ResponseEntity.ok(treatments);
    }

    @DeleteMapping("/{treatmentId}")
    @Operation(
            summary = "Delete a treatment",
            description = "Delete a treatment record. Doctor only. Use with caution - this is a hard delete."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Treatment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Treatment not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteTreatment(
            @Parameter(description = "Treatment UUID") @PathVariable UUID treatmentId
    ) {
        log.info("DELETE /api/treatments/{} - Deleting treatment", treatmentId);
        treatmentService.delete(treatmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patients/{patientId}/count")
    @Operation(
            summary = "Count treatments for a patient",
            description = "Get the total number of treatment records for a patient"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Long> countTreatments(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        log.info("GET /api/treatments/patients/{}/count", patientId);
        long count = treatmentService.countByPatientId(patientId);
        return ResponseEntity.ok(count);
    }
}
