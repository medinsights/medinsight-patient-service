package com.medinsights.patient_service.services;

import com.medinsights.patient_service.dto.patient.PatientCreateRequest;
import com.medinsights.patient_service.dto.patient.PatientUpdateRequest;
import com.medinsights.patient_service.entities.Patient;
import com.medinsights.patient_service.exceptions.ResourceNotFoundException;
import com.medinsights.patient_service.exceptions.UnauthorizedException;
import com.medinsights.patient_service.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository repository;

    public Patient create(PatientCreateRequest request, UUID userId) {
        Patient patient = new Patient();
        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setPhone(request.phone());
        patient.setEmail(request.email());
        patient.setAddress(request.address());
        patient.setCity(request.city());
        patient.setPostalCode(request.postalCode());
        patient.setCountry(request.country());
        patient.setBloodGroup(request.bloodGroup());
        patient.setFamilyHistory(request.familyHistory());
        patient.setAllergies(request.allergies());
        patient.setChronicDiseases(request.chronicDiseases());
        patient.setEmergencyContactName(request.emergencyContactName());
        patient.setEmergencyContactPhone(request.emergencyContactPhone());
        patient.setAttendingPhysician(request.attendingPhysician());
        patient.setNotes(request.notes());
        patient.setCreatedBy(userId);
        patient.setActive(true);

        return repository.save(patient);
    }

    @Transactional(readOnly = true)
    public Patient findById(UUID patientId, UUID userId) {
        Patient patient = repository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        if (!patient.getCreatedBy().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to access this patient");
        }

        return patient;
    }

    @Transactional(readOnly = true)
    public List<Patient> findMyPatients(UUID userId, Boolean activeOnly) {
        if (activeOnly != null && activeOnly) {
            return repository.findByCreatedByAndActiveTrue(userId);
        }
        return repository.findByCreatedBy(userId);
    }

    @Transactional(readOnly = true)
    public List<Patient> searchPatients(String search, UUID userId) {
        return repository.searchPatients(search, userId);
    }

    public Patient update(UUID patientId, PatientUpdateRequest request, UUID userId) {
        Patient patient = findById(patientId, userId);

        if (request.firstName() != null) patient.setFirstName(request.firstName());
        if (request.lastName() != null) patient.setLastName(request.lastName());
        if (request.dateOfBirth() != null) patient.setDateOfBirth(request.dateOfBirth());
        if (request.gender() != null) patient.setGender(request.gender());
        if (request.phone() != null) patient.setPhone(request.phone());
        if (request.email() != null) patient.setEmail(request.email());
        if (request.address() != null) patient.setAddress(request.address());
        if (request.city() != null) patient.setCity(request.city());
        if (request.postalCode() != null) patient.setPostalCode(request.postalCode());
        if (request.country() != null) patient.setCountry(request.country());
        if (request.bloodGroup() != null) patient.setBloodGroup(request.bloodGroup());
        if (request.familyHistory() != null) patient.setFamilyHistory(request.familyHistory());
        if (request.allergies() != null) patient.setAllergies(request.allergies());
        if (request.chronicDiseases() != null) patient.setChronicDiseases(request.chronicDiseases());
        if (request.emergencyContactName() != null) patient.setEmergencyContactName(request.emergencyContactName());
        if (request.emergencyContactPhone() != null) patient.setEmergencyContactPhone(request.emergencyContactPhone());
        if (request.attendingPhysician() != null) patient.setAttendingPhysician(request.attendingPhysician());
        if (request.notes() != null) patient.setNotes(request.notes());
        if (request.active() != null) patient.setActive(request.active());

        patient.setUpdatedBy(userId);

        return repository.save(patient);
    }

    public void delete(UUID patientId, UUID userId) {
        Patient patient = findById(patientId, userId);
        repository.delete(patient);
    }

    public void deactivate(UUID patientId, UUID userId) {
        Patient patient = findById(patientId, userId);
        patient.setActive(false);
        patient.setUpdatedBy(userId);
        repository.save(patient);
    }

    @Transactional(readOnly = true)
    public long countActivePatients(UUID userId) {
        return repository.countActivePatientsByUser(userId);
    }
}
