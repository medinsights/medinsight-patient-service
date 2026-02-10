package com.medinsights.patient_service.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * Medical Alert entity for tracking patient health alerts
 * Used for critical values, drug interactions, follow-up reminders, etc.
 */
@Entity
@Table(name = "medical_alerts", indexes = {
        @Index(name = "idx_alert_patient_status", columnList = "patient_id, status"),
        @Index(name = "idx_alert_severity", columnList = "severityLevel"),
        @Index(name = "idx_alert_created", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotBlank(message = "Alert type is required")
    @Size(max = 100, message = "Alert type cannot exceed 100 characters")
    @Column(name = "alert_type", nullable = false, length = 100)
    private String alertType; // CRITICAL_VALUE, DRUG_INTERACTION, FOLLOW_UP, etc.

    @Size(max = 20, message = "Severity level cannot exceed 20 characters")
    @Column(name = "severity_level", length = 20)
    private String severityLevel; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(columnDefinition = "TEXT")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(name = "resolution_date")
    private Instant resolutionDate;

    @NotBlank(message = "Status is required")
    @Size(max = 20, message = "Status cannot exceed 20 characters")
    @Column(nullable = false, length = 20)
    private String status = "active"; // active, resolved, dismissed

    @Column(name = "required_action", columnDefinition = "TEXT")
    private String requiredAction;

    @Column(nullable = false, updatable = false)
    private UUID createdBy; // userId who created the alert

    @Column
    private UUID resolvedBy; // userId who resolved the alert

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(name = "updated_at")
    private Instant updatedAt;
}
