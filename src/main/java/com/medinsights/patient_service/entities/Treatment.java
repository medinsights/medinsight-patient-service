package com.medinsights.patient_service.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Treatment entity representing patient medication and prescriptions
 * Supports US-1.3: Medical History & Follow-up
 */
@Entity
@Table(name = "treatments", indexes = {
        @Index(name = "idx_treatment_patient", columnList = "patient_id"),
        @Index(name = "idx_treatment_status", columnList = "status"),
        @Index(name = "idx_treatment_start_date", columnList = "startDate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Treatment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotBlank(message = "Medication name is required")
    @Size(min = 2, max = 200, message = "Medication name must be between 2 and 200 characters")
    @Column(nullable = false, length = 200)
    private String medicationName;

    @NotBlank(message = "Dosage is required")
    @Size(max = 100, message = "Dosage cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String dosage;

    @Size(max = 100, message = "Frequency cannot exceed 100 characters")
    @Column(length = 100)
    private String frequency;

    @Size(max = 50, message = "Route of administration cannot exceed 50 characters")
    @Column(length = 50)
    private String routeOfAdministration = "ORAL"; // ORAL, INTRAVENOUS, TOPICAL, etc.

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private Integer durationDays;

    @NotBlank(message = "Status is required")
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, COMPLETED, DISCONTINUED, PAUSED

    @Size(max = 500, message = "Indication cannot exceed 500 characters")
    @Column(length = 500)
    private String indication; // Medical reason for prescription

    @Size(max = 500, message = "Side effects cannot exceed 500 characters")
    @Column(length = 500)
    private String sideEffects;

    @Size(max = 200, message = "Prescriber name cannot exceed 200 characters")
    @Column(length = 200)
    private String prescriberName;

    @Column(length = 1000)
    private String notes;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false, updatable = false)
    private UUID createdBy; // Doctor or user who prescribed

    @Column
    private UUID updatedBy;
}
