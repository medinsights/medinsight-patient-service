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

@Entity
@Table(name = "medical_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Diagnosis date is required")
    @Column(nullable = false)
    private LocalDate diagnosisDate;

    @NotBlank(message = "Diagnosis is required")
    @Size(min = 3, max = 200, message = "Diagnosis must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String diagnosis;

    @Column(length = 1000)
    private String symptoms;

    @Column(length = 1000)
    private String treatment;

    @Column(length = 500)
    private String medications;

    @Column(length = 1000)
    private String notes;

    @Column(length = 50)
    private String severity; // MILD, MODERATE, SEVERE

    @Column
    private Boolean resolved = false;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false, updatable = false)
    private UUID createdBy; // Doctor who added this record

    @Column
    private UUID updatedBy;
}
