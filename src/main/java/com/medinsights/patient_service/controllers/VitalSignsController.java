package com.medinsights.patient_service.controllers;

import com.medinsights.patient_service.dto.vitalsigns.VitalSignsCreateRequest;
import com.medinsights.patient_service.dto.vitalsigns.VitalSignsResponse;
import com.medinsights.patient_service.services.VitalSignsService;
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
 * REST Controller for Vital Signs management
 * Supports US-1.3: Medical History & Follow-up Management
 */
@RestController
@RequestMapping("/api/vital-signs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vital Signs Management", description = "APIs for recording and tracking patient vital signs (US-1.3)")
public class VitalSignsController {

    private final VitalSignsService vitalSignsService;

    @PostMapping("/patients/{patientId}")
    @Operation(
            summary = "Record new vital signs",
            description = "Record vital signs measurements for a patient. BMI is automatically calculated."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Vital signs recorded successfully",
                    content = @Content(schema = @Schema(implementation = VitalSignsResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid")
    })
    public ResponseEntity<VitalSignsResponse> recordVitalSigns(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Valid @RequestBody VitalSignsCreateRequest request,
            @RequestAttribute("userId") UUID userId
    ) {
        log.info("POST /api/vital-signs/patients/{} - Recording vital signs", patientId);
        VitalSignsResponse response = vitalSignsService.create(patientId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{vitalSignsId}")
    @Operation(
            summary = "Get vital signs by ID",
            description = "Retrieve specific vital signs measurement record"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Vital signs found",
                    content = @Content(schema = @Schema(implementation = VitalSignsResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Vital signs not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<VitalSignsResponse> getVitalSignsById(
            @Parameter(description = "Vital Signs UUID") @PathVariable UUID vitalSignsId
    ) {
        log.info("GET /api/vital-signs/{} - Fetching vital signs", vitalSignsId);
        VitalSignsResponse response = vitalSignsService.findById(vitalSignsId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients/{patientId}")
    @Operation(
            summary = "Get all vital signs for a patient",
            description = "Retrieve all vital signs records for a specific patient, ordered by measurement date (most recent first)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Vital signs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = VitalSignsResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<VitalSignsResponse>> getPatientVitalSigns(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        log.info("GET /api/vital-signs/patients/{} - Fetching all vital signs", patientId);
        List<VitalSignsResponse> vitalSigns = vitalSignsService.findByPatientId(patientId);
        return ResponseEntity.ok(vitalSigns);
    }

    @GetMapping("/patients/{patientId}/latest")
    @Operation(
            summary = "Get latest vital signs for a patient",
            description = "Retrieve the most recent vital signs measurement for a patient"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Latest vital signs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = VitalSignsResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "No vital signs found for patient"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<VitalSignsResponse> getLatestVitalSigns(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        log.info("GET /api/vital-signs/patients/{}/latest - Fetching latest vital signs", patientId);
        return vitalSignsService.findLatestByPatientId(patientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patients/{patientId}/date-range")
    @Operation(
            summary = "Get vital signs by date range",
            description = "Retrieve vital signs for a patient within a specific date range"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Vital signs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = VitalSignsResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid date range"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<VitalSignsResponse>> getVitalSignsByDateRange(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Parameter(description = "Start date (ISO format: yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (ISO format: yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("GET /api/vital-signs/patients/{}/date-range - Start: {}, End: {}", patientId, startDate, endDate);
        List<VitalSignsResponse> vitalSigns = vitalSignsService.findByPatientIdAndDateRange(patientId, startDate, endDate);
        return ResponseEntity.ok(vitalSigns);
    }

    @DeleteMapping("/{vitalSignsId}")
    @Operation(
            summary = "Delete vital signs record",
            description = "Delete a vital signs measurement record. Use with caution - this is a hard delete."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vital signs deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vital signs not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> deleteVitalSigns(
            @Parameter(description = "Vital Signs UUID") @PathVariable UUID vitalSignsId
    ) {
        log.info("DELETE /api/vital-signs/{} - Deleting vital signs", vitalSignsId);
        vitalSignsService.delete(vitalSignsId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patients/{patientId}/count")
    @Operation(
            summary = "Count vital signs records for a patient",
            description = "Get the total number of vital signs measurements for a patient"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Long> countVitalSigns(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId
    ) {
        log.info("GET /api/vital-signs/patients/{}/count", patientId);
        long count = vitalSignsService.countByPatientId(patientId);
        return ResponseEntity.ok(count);
    }
}
