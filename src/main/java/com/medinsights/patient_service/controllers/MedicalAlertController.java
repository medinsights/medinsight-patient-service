package com.medinsights.patient_service.controllers;

import com.medinsights.patient_service.dto.CreateMedicalAlertDTO;
import com.medinsights.patient_service.dto.MedicalAlertDTO;
import com.medinsights.patient_service.services.MedicalAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing medical alerts
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Medical Alerts", description = "Endpoints for managing patient medical alerts")
@SecurityRequirement(name = "Bearer Authentication")
public class MedicalAlertController {
    
    private final MedicalAlertService alertService;
    
    @GetMapping("/patients/{patientId}/alerts")
    @Operation(
        summary = "Get all alerts for a patient",
        description = "Retrieve all medical alerts for a specific patient",
        responses = {
            @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
        }
    )
    public ResponseEntity<List<MedicalAlertDTO>> getPatientAlerts(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId) {
        List<MedicalAlertDTO> alerts = alertService.getPatientAlerts(patientId);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/patients/{patientId}/alerts/active")
    @Operation(
        summary = "Get active alerts for a patient",
        description = "Retrieve only active (unresolved) alerts for a specific patient"
    )
    public ResponseEntity<List<MedicalAlertDTO>> getActiveAlerts(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId) {
        List<MedicalAlertDTO> alerts = alertService.getActiveAlerts(patientId);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/patients/{patientId}/alerts/severity/{severity}")
    @Operation(
        summary = "Get alerts by severity",
        description = "Retrieve alerts filtered by severity level (LOW, MEDIUM, HIGH, CRITICAL)"
    )
    public ResponseEntity<List<MedicalAlertDTO>> getAlertsBySeverity(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Parameter(description = "Severity level") @PathVariable String severity) {
        List<MedicalAlertDTO> alerts = alertService.getAlertsBySeverity(patientId, severity);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/patients/{patientId}/alerts/count")
    @Operation(
        summary = "Count active alerts",
        description = "Get the count of active alerts for a patient"
    )
    public ResponseEntity<Long> countActiveAlerts(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId) {
        long count = alertService.countActiveAlerts(patientId);
        return ResponseEntity.ok(count);
    }
    
    @PostMapping("/patients/{patientId}/alerts")
    @Operation(
        summary = "Create a new alert",
        description = "Create a new medical alert for a patient",
        responses = {
            @ApiResponse(responseCode = "201", description = "Alert created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
        }
    )
    public ResponseEntity<MedicalAlertDTO> createAlert(
            @Parameter(description = "Patient UUID") @PathVariable UUID patientId,
            @Valid @RequestBody CreateMedicalAlertDTO dto,
            @RequestHeader("X-User-Id") UUID userId) {
        
        dto.setPatientId(patientId);
        MedicalAlertDTO createdAlert = alertService.createAlert(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAlert);
    }
    
    @PutMapping("/alerts/{alertId}/resolve")
    @Operation(
        summary = "Resolve an alert",
        description = "Mark an alert as resolved"
    )
    public ResponseEntity<MedicalAlertDTO> resolveAlert(
            @Parameter(description = "Alert UUID") @PathVariable UUID alertId,
            @RequestHeader("X-User-Id") UUID userId) {
        MedicalAlertDTO resolvedAlert = alertService.resolveAlert(alertId, userId);
        return ResponseEntity.ok(resolvedAlert);
    }
    
    @PutMapping("/alerts/{alertId}/dismiss")
    @Operation(
        summary = "Dismiss an alert",
        description = "Dismiss an alert without resolving it"
    )
    public ResponseEntity<MedicalAlertDTO> dismissAlert(
            @Parameter(description = "Alert UUID") @PathVariable UUID alertId,
            @RequestHeader("X-User-Id") UUID userId) {
        MedicalAlertDTO dismissedAlert = alertService.dismissAlert(alertId, userId);
        return ResponseEntity.ok(dismissedAlert);
    }
    
    @DeleteMapping("/alerts/{alertId}")
    @Operation(
        summary = "Delete an alert",
        description = "Permanently delete an alert"
    )
    public ResponseEntity<Void> deleteAlert(
            @Parameter(description = "Alert UUID") @PathVariable UUID alertId) {
        alertService.deleteAlert(alertId);
        return ResponseEntity.noContent().build();
    }
}
