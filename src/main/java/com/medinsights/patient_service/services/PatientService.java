package com.medinsights.patient_service.services;

import com.medinsights.patient_service.dto.PatientCreateRequest;
import com.medinsights.patient_service.dto.PatientUpdateRequest;
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
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());
        patient.setCity(request.getCity());
        patient.setPostalCode(request.getPostalCode());
        patient.setCountry(request.getCountry());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setAllergies(request.getAllergies());
        patient.setChronicDiseases(request.getChronicDiseases());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        patient.setNotes(request.getNotes());
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

        if (request.getFirstName() != null) patient.setFirstName(request.getFirstName());
        if (request.getLastName() != null) patient.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) patient.setGender(request.getGender());
        if (request.getPhone() != null) patient.setPhone(request.getPhone());
        if (request.getEmail() != null) patient.setEmail(request.getEmail());
        if (request.getAddress() != null) patient.setAddress(request.getAddress());
        if (request.getCity() != null) patient.setCity(request.getCity());
        if (request.getPostalCode() != null) patient.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) patient.setCountry(request.getCountry());
        if (request.getBloodGroup() != null) patient.setBloodGroup(request.getBloodGroup());
        if (request.getAllergies() != null) patient.setAllergies(request.getAllergies());
        if (request.getChronicDiseases() != null) patient.setChronicDiseases(request.getChronicDiseases());
        if (request.getEmergencyContactName() != null) patient.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactPhone() != null) patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        if (request.getNotes() != null) patient.setNotes(request.getNotes());
        if (request.getActive() != null) patient.setActive(request.getActive());

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
