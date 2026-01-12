package com.medinsights.patient_service.config;

import com.medinsights.patient_service.entities.Patient;
import com.medinsights.patient_service.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Database seeder for development/testing
 * Automatically seeds fake patients if database is empty
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final PatientRepository patientRepository;

    @Bean
    CommandLineRunner seedDatabase() {
        return args -> {
            // Check if patients already exist
            long existingCount = patientRepository.count();
            
            if (existingCount > 0) {
                log.info("Database already contains {} patients. Skipping seeding.", existingCount);
                return;
            }

            log.info("Seeding fake patients...");

            // UUID of testuser from auth service
            UUID testUserId = UUID.fromString("cb73662b-cd84-4872-8220-5d09051d756e");

            List<Patient> fakePatients = Arrays.asList(
                createPatient(
                    "John", "Doe",
                    LocalDate.of(1985, 5, 15),
                    "MALE",
                    "+1234567890",
                    "john.doe@email.com",
                    "123 Main St",
                    "New York",
                    "10001",
                    "USA",
                    "A+",
                    testUserId
                ),
                createPatient(
                    "Jane", "Smith",
                    LocalDate.of(1990, 8, 22),
                    "FEMALE",
                    "+1234567891",
                    "jane.smith@email.com",
                    "456 Oak Ave",
                    "Los Angeles",
                    "90001",
                    "USA",
                    "B+",
                    testUserId
                ),
                createPatient(
                    "Michael", "Johnson",
                    LocalDate.of(1978, 3, 10),
                    "MALE",
                    "+1234567892",
                    "michael.j@email.com",
                    "789 Pine Rd",
                    "Chicago",
                    "60601",
                    "USA",
                    "O+",
                    testUserId
                ),
                createPatient(
                    "Emily", "Davis",
                    LocalDate.of(1995, 11, 5),
                    "FEMALE",
                    "+1234567893",
                    "emily.davis@email.com",
                    "321 Elm St",
                    "Houston",
                    "77001",
                    "USA",
                    "AB+",
                    testUserId
                ),
                createPatient(
                    "Robert", "Wilson",
                    LocalDate.of(1982, 7, 18),
                    "MALE",
                    "+1234567894",
                    "robert.w@email.com",
                    "654 Maple Dr",
                    "Phoenix",
                    "85001",
                    "USA",
                    "A-",
                    testUserId
                )
            );

            patientRepository.saveAll(fakePatients);
            
            log.info("✅ Successfully seeded {} patients!", fakePatients.size());
            fakePatients.forEach(p -> 
                log.info("  ✓ Created patient: {} {} ({})", 
                    p.getFirstName(), p.getLastName(), p.getEmail())
            );
        };
    }

    private Patient createPatient(String firstName, String lastName, LocalDate dob,
                                  String gender, String phone, String email,
                                  String address, String city, String postalCode,
                                  String country, String bloodGroup, UUID createdBy) {
        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setDateOfBirth(dob);
        patient.setGender(gender);
        patient.setPhone(phone);
        patient.setEmail(email);
        patient.setAddress(address);
        patient.setCity(city);
        patient.setPostalCode(postalCode);
        patient.setCountry(country);
        patient.setBloodGroup(bloodGroup);
        patient.setCreatedBy(createdBy);
        return patient;
    }
}
