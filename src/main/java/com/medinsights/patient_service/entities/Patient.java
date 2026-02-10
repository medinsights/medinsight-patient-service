package com.medinsights.patient_service.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Patient entity representing patient demographics and basic information
 * Supports US-1.1, US-1.2: Unified Patient Management
 */

@Entity
@Table(name = "patients", indexes = {
        @Index(name = "idx_patient_created_by", columnList = "createdBy"),
        @Index(name = "idx_patient_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE, or OTHER")
    @Column(length = 10, nullable = false)
    private String gender;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Column(length = 20)
    private String phone;

    @Email(message = "Invalid email format")
    @Column(unique = true, length = 100)
    private String email;

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    @Column(length = 200)
    private String address;

    @Size(max = 50, message = "City cannot exceed 50 characters")
    @Column(length = 50)
    private String city;

    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Column(length = 20)
    private String postalCode;

    @Size(max = 50, message = "Country cannot exceed 50 characters")
    @Column(length = 50)
    private String country;

    @Size(max = 20, message = "Blood group cannot exceed 20 characters")
    @Column(length = 20)
    private String bloodGroup;

    @Column(length = 500)
    private String familyHistory; // antecedents_familiaux from chatbot

    @Column(length = 500)
    private String allergies;

    @Column(length = 500)
    private String chronicDiseases;

    @Column(length = 1000)
    private String mainPathologies; // pathologies_principales - primary medical conditions

    @Size(max = 20, message = "Status cannot exceed 20 characters")
    @Column(length = 20)
    private String status = "active"; // active, inactive - patient status

    @Column(length = 100)
    private String emergencyContactName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid emergency phone number format")
    @Column(length = 20)
    private String emergencyContactPhone;

    @Column(length = 1000)
    private String notes;

    @Size(max = 200, message = "Attending physician name cannot exceed 200 characters")
    @Column(length = 200)
    private String attendingPhysician; // medecin_traitant from chatbot

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false, updatable = false)
    private UUID createdBy; // userId from JWT (Doctor or Secretary)

    @Column
    private UUID updatedBy;

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalHistory> medicalHistories = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consultation> consultations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Treatment> treatments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VitalSigns> vitalSigns = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalAnalysis> medicalAnalyses = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalAlert> medicalAlerts = new ArrayList<>();
}

