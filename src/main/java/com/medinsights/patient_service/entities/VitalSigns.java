package com.medinsights.patient_service.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * VitalSigns entity representing patient vital measurements
 * Supports US-1.3: Medical History & Follow-up
 */
@Entity
@Table(name = "vital_signs", indexes = {
        @Index(name = "idx_vital_signs_patient", columnList = "patient_id"),
        @Index(name = "idx_vital_signs_measurement_date", columnList = "measurementDate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VitalSigns {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Measurement date is required")
    @Column(nullable = false)
    private LocalDateTime measurementDate;

    // Blood Pressure
    @Min(value = 50, message = "Systolic BP must be at least 50 mmHg")
    @Max(value = 250, message = "Systolic BP cannot exceed 250 mmHg")
    @Column
    private Integer systolicBP; // mmHg

    @Min(value = 30, message = "Diastolic BP must be at least 30 mmHg")
    @Max(value = 150, message = "Diastolic BP cannot exceed 150 mmHg")
    @Column
    private Integer diastolicBP; // mmHg

    // Heart Rate
    @Min(value = 30, message = "Heart rate must be at least 30 bpm")
    @Max(value = 250, message = "Heart rate cannot exceed 250 bpm")
    @Column
    private Integer heartRate; // beats per minute

    // Temperature
    @Min(value = 30, message = "Temperature must be at least 30°C")
    @Max(value = 45, message = "Temperature cannot exceed 45°C")
    @Column
    private Double temperature; // Celsius

    // Weight and Height
    @Positive(message = "Weight must be positive")
    @Column
    private Double weight; // kg

    @Positive(message = "Height must be positive")
    @Column
    private Double height; // cm

    @Column
    private Double bmi; // Calculated: weight(kg) / (height(m))^2

    // Respiratory Rate
    @Min(value = 5, message = "Respiratory rate must be at least 5/min")
    @Max(value = 60, message = "Respiratory rate cannot exceed 60/min")
    @Column
    private Integer respiratoryRate; // breaths per minute

    // Oxygen Saturation
    @Min(value = 50, message = "Oxygen saturation must be at least 50%")
    @Max(value = 100, message = "Oxygen saturation cannot exceed 100%")
    @Column
    private Integer oxygenSaturation; // SpO2 percentage

    // Blood Glucose
    @Column
    private Double bloodGlucose; // mmol/L or mg/dL

    @Column(length = 500)
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
    private UUID createdBy; // Healthcare professional who recorded the measurements

    @Column
    private UUID updatedBy;

    /**
     * Calculate and update BMI if weight and height are present
     */
    @PrePersist
    @PreUpdate
    public void calculateBMI() {
        if (weight != null && height != null && height > 0) {
            double heightInMeters = height / 100.0;
            this.bmi = weight / (heightInMeters * heightInMeters);
            // Round to 2 decimal places
            this.bmi = Math.round(bmi * 100.0) / 100.0;
        }
    }
}
