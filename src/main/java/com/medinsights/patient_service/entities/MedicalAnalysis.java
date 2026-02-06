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
 * MedicalAnalysis entity representing lab results and medical test reports
 * Supports US-1.3: Medical History & Follow-up
 */
@Entity
@Table(name = "medical_analyses", indexes = {
        @Index(name = "idx_medical_analysis_patient", columnList = "patient_id"),
        @Index(name = "idx_medical_analysis_type", columnList = "analysisType"),
        @Index(name = "idx_medical_analysis_date", columnList = "analysisDate")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotBlank(message = "Analysis type is required")
    @Size(max = 100, message = "Analysis type cannot exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String analysisType; // BLOOD_TEST, URINE_TEST, X_RAY, MRI, CT_SCAN, ECG, etc.

    @NotNull(message = "Analysis date is required")
    @Column(nullable = false)
    private LocalDate analysisDate;

    @Size(max = 200, message = "File name cannot exceed 200 characters")
    @Column(length = 200)
    private String fileName; // Original document filename

    @Column(columnDefinition = "TEXT")
    private String ocrText; // texte_ocr from chatbot - extracted text from PDF/image

    @Column(columnDefinition = "TEXT")
    private String results; // JSON or structured data with test results

    @Column(columnDefinition = "TEXT")
    private String interpretation; // Doctor's interpretation of results

    @Column(columnDefinition = "TEXT")
    private String alertsAndAnomalies; // Any critical findings or abnormal values

    @Column(columnDefinition = "TEXT")
    private String recommendations; // recommandations from chatbot - AI-generated recommendations

    @Column(length = 200)
    private String performedBy; // Lab or healthcare facility

    @Column(length = 200)
    private String interpretedBy; // Doctor who interpreted results

    @Column(length = 50)
    private String status = "PENDING"; // PENDING, COMPLETED, REVIEWED

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
    private UUID createdBy; // User who uploaded/created the analysis

    @Column
    private UUID updatedBy;
}
