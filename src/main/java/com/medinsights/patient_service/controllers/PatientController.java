package com.medinsights.patient_service.controllers;

import com.medinsights.patient_service.dto.patient.PatientCreateRequest;
import com.medinsights.patient_service.dto.patient.PatientUpdateRequest;
import com.medinsights.patient_service.entities.Patient;
import com.medinsights.patient_service.services.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "APIs for managing patient information (US-1.1, US-1.2)")
public class PatientController {

    private final PatientService service;

    @PostMapping
    @Operation(
            summary = "Create a new patient",
            description = "Creates a new patient record. Accessible by Doctors and Secretaries."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Patient> create(
            @RequestBody @Valid PatientCreateRequest request,
            @Parameter(hidden = true) @RequestAttribute("userId") UUID userId
    ) {
        Patient patient = service.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    @GetMapping
    @Operation(
            summary = "List all patients",
            description = "Retrieves all patients created by the authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of patients")
    public ResponseEntity<List<Patient>> list(
            @Parameter(description = "Filter only active patients")
            @RequestParam(required = false) Boolean activeOnly,
            @Parameter(hidden = true) @RequestAttribute("userId") UUID userId
    ) {
        List<Patient> patients = service.findMyPatients(userId, activeOnly);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{patientId}")
    @Operation(
            summary = "Get patient by ID",
            description = "Retrieves detailed information about a specific patient"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to access this patient")
    })
    public ResponseEntity<Patient> getById(
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @RequestAttribute("userId") UUID userId
    ) {
        Patient patient = service.findById(patientId, userId);
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search patients",
            description = "Search patients by name or email"
    )
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<List<Patient>> search(
            @Parameter(description = "Search term (name or email)")
            @RequestParam String query,
            @Parameter(hidden = true) @RequestAttribute("userId") UUID userId
    ) {
        List<Patient> patients = service.searchPatients(query, userId);
        return ResponseEntity.ok(patients);
    }

    @PutMapping("/{patientId}")
    @Operation(
            summary = "Update patient information",
            description = "Updates existing patient record. Only the user who created the patient can update it."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to update this patient")
    })
    public ResponseEntity<Patient> update(
            @PathVariable UUID patientId,
            @RequestBody @Valid PatientUpdateRequest request,
            @Parameter(hidden = true) @RequestAttribute("userId") UUID userId
    ) {
        Patient patient = service.update(patientId, request, userId);
        return ResponseEntity.ok(patient);
    }

    @DeleteMapping("/{patientId}")
    @Operation(
            summary = "Delete patient",
            description = "Permanently deletes a patient record"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to delete this patient")
    })
    public ResponseEntity<Void> delete(
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @RequestAttribute("userId") UUID userId
    ) {
        service.delete(patientId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{patientId}/deactivate")
    @Operation(
            summary = "Deactivate patient",
            description = "Marks a patient as inactive (soft delete)"
    )
    @ApiResponse(responseCode = "204", description = "Patient deactivated successfully")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @RequestAttribute("userId") UUID userId
    ) {
        service.deactivate(patientId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/count")
    @Operation(
            summary = "Get patient count",
            description = "Returns the count of active patients for the authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "Patient count retrieved successfully")
    public ResponseEntity<Long> countActivePatients(
            @Parameter(hidden = true) @RequestAttribute("userId") UUID userId
    ) {
        long count = service.countActivePatients(userId);
        return ResponseEntity.ok(count);
    }
}
