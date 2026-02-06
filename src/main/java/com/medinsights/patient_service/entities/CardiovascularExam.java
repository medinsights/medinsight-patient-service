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

/**
 * Entity for cardiovascular examinations
 * Matches chatbot examens_cardiovasculaires table
 */
@Entity
@Table(name = "cardiovascular_exams", indexes = {
        @Index(name = "idx_cardiovascular_patient", columnList = "patient_id"),
        @Index(name = "idx_cardiovascular_date", columnList = "examDate"),
        @Index(name = "idx_cardiovascular_type", columnList = "examType")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardiovascularExam {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotBlank(message = "Exam type is required")
    @Size(min = 2, max = 100, message = "Exam type must be between 2 and 100 characters")
    @Column(name = "exam_type", nullable = false, length = 100)
    private String examType; // type_examen: ECG, Echocardiography, Stress Test, etc.

    @NotNull(message = "Exam date is required")
    @Column(name = "exam_date", nullable = false)
    private LocalDateTime examDate; // date_examen

    @NotBlank(message = "Results are required")
    @Size(min = 3, max = 2000, message = "Results must be between 3 and 2000 characters")
    @Column(nullable = false, length = 2000)
    private String results; // resultats

    @Column(length = 2000)
    private String interpretation; // interpretation - medical professional interpretation

    @Column(name = "measured_values", length = 1000)
    private String measuredValues; // valeurs_mesurees - JSON or structured data

    @Column(length = 1000)
    private String abnormalities; // anomalies - detected abnormalities

    @Column(name = "pdf_file", length = 500)
    private String pdfFile; // fichier_pdf - path/URL to PDF report

    @Column(length = 500)
    private String notes; // Additional notes

    @Column(length = 50)
    private String status = "COMPLETED"; // PENDING, IN_PROGRESS, COMPLETED, REVIEWED

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(nullable = false)
    private Instant updatedAt;
}
