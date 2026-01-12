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
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consultations", indexes = {
        @Index(name = "idx_consultation_patient", columnList = "patient_id"),
        @Index(name = "idx_consultation_date", columnList = "consultationDate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Consultation date is required")
    @Column(nullable = false)
    private LocalDateTime consultationDate;

    @NotBlank(message = "Reason for visit is required")
    @Size(min = 3, max = 200, message = "Reason must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String reasonForVisit;

    @Column(length = 1000)
    private String symptoms;

    @Column(length = 1000)
    private String diagnosis;

    @Column(length = 1000)
    private String treatment;

    @Column(length = 500)
    private String prescriptions;

    @Column(length = 1000)
    private String notes;

    @Column(length = 500)
    private String vitalSigns; // Blood pressure, temperature, etc.

    @Column(length = 200)
    private String followUpInstructions;

    @Column
    private LocalDateTime nextAppointment;

    @Column(nullable = false)
    private String status = "COMPLETED"; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false, updatable = false)
    private UUID createdBy; // Doctor who conducted the consultation

    @Column
    private UUID updatedBy;
}
