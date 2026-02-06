package com.medinsights.patient_service.dto.patient;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Request DTO for creating a new patient
 * Supports US-1.1, US-1.2: Unified Patient Management (Doctor & Secretary)
 */
public record PatientCreateRequest(
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @NotBlank(message = "Gender is required")
        @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE, or OTHER")
        String gender,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
        String phone,

        @Email(message = "Invalid email format")
        String email,

        @Size(max = 200, message = "Address cannot exceed 200 characters")
        String address,

        @Size(max = 50, message = "City cannot exceed 50 characters")
        String city,

        @Size(max = 20, message = "Postal code cannot exceed 20 characters")
        String postalCode,

        @Size(max = 50, message = "Country cannot exceed 50 characters")
        String country,

        @Size(max = 20, message = "Blood group cannot exceed 20 characters")
        String bloodGroup,

        @Size(max = 500, message = "Family history cannot exceed 500 characters")
        String familyHistory,

        @Size(max = 500, message = "Allergies cannot exceed 500 characters")
        String allergies,

        @Size(max = 500, message = "Chronic diseases cannot exceed 500 characters")
        String chronicDiseases,

        @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
        String emergencyContactName,

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid emergency phone number format")
        String emergencyContactPhone,

        @Size(max = 200, message = "Attending physician name cannot exceed 200 characters")
        String attendingPhysician,

        String notes
) {
    /**
     * Compact constructor for validation and normalization
     */
    public PatientCreateRequest {
        // Trim and normalize string inputs
        if (firstName != null) {
            firstName = firstName.trim();
        }
        if (lastName != null) {
            lastName = lastName.trim();
        }
        if (gender != null) {
            gender = gender.trim().toUpperCase();
        }
        if (email != null) {
            email = email.trim().toLowerCase();
        }
    }
}
