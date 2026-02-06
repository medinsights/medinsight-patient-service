package com.medinsights.patient_service.config;

import com.medinsights.patient_service.entities.*;
import com.medinsights.patient_service.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Database seeder for development/testing
 * Seeds all tables: Patients, VitalSigns, Consultations, Treatments, MedicalAnalyses, CardiovascularExams
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final PatientRepository patientRepository;
    private final VitalSignsRepository vitalSignsRepository;
    private final ConsultationRepository consultationRepository;
    private final TreatmentRepository treatmentRepository;
    private final MedicalAnalysisRepository medicalAnalysisRepository;
    private final CardiovascularExamRepository cardiovascularExamRepository;

    @Bean
    CommandLineRunner seedDatabase() {
        return args -> {
            // Check if data already exists
            long existingPatients = patientRepository.count();
            
            if (existingPatients > 0) {
                log.info("Database already contains {} patients. Skipping seeding.", existingPatients);
                return;
            }

            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("  🌱 Seeding Patient Service Database");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            // UUID of testuser from auth service
            UUID testUserId = UUID.fromString("cb73662b-cd84-4872-8220-5d09051d756e");

            // ================================================================
            // STEP 1: CREATE PATIENTS
            // ================================================================
            log.info("\n📋 Step 1: Creating Patients...");
            
            List<Patient> patients = Arrays.asList(
                createPatient(
                    "John", "Doe",
                    LocalDate.of(1985, 5, 15),
                    "MALE",
                    "+1234567890",
                    "john.doe@email.com",
                    "123 Main St", "New York", "10001", "USA",
                    "A+",
                    "Peanut allergy",
                    "Hypertension, Type 2 Diabetes",
                    "Dr. Sarah Johnson",
                    "Emergency: Mary Doe +1234567800",
                    testUserId
                ),
                createPatient(
                    "Jane", "Smith",
                    LocalDate.of(1990, 8, 22),
                    "FEMALE",
                    "+1234567891",
                    "jane.smith@email.com",
                    "456 Oak Ave", "Los Angeles", "90001", "USA",
                    "B+",
                    "Penicillin allergy",
                    "Asthma",
                    "Dr. Michael Chen",
                    "Emergency: Robert Smith +1234567801",
                    testUserId
                ),
                createPatient(
                    "Michael", "Johnson",
                    LocalDate.of(1978, 3, 10),
                    "MALE",
                    "+1234567892",
                    "michael.j@email.com",
                    "789 Pine Rd", "Chicago", "60601", "USA",
                    "O+",
                    "No known allergies",
                    "Hypercholesterolemia, Obesity",
                    "Dr. Emily Rodriguez",
                    "Emergency: Lisa Johnson +1234567802",
                    testUserId
                )
            );

            patients = patientRepository.saveAll(patients);
            log.info("✅ Created {} patients", patients.size());

            // ================================================================
            // STEP 2: CREATE VITAL SIGNS
            // ================================================================
            log.info("\n💉 Step 2: Creating Vital Signs...");
            
            List<VitalSigns> vitalSigns = Arrays.asList(
                // John Doe - slightly elevated BP and glucose (diabetic + hypertensive)
                createVitalSigns(patients.get(0), LocalDateTime.now().minusDays(7),
                    145, 92, 78, 36.7, 98, 85.0, 175.0, 27.8, 135),
                
                // John Doe - follow-up (better control)
                createVitalSigns(patients.get(0), LocalDateTime.now().minusDays(1),
                    138, 88, 72, 36.5, 99, 84.5, 175.0, 27.6, 120),
                
                // Jane Smith - normal vitals (asthma patient)
                createVitalSigns(patients.get(1), LocalDateTime.now().minusDays(10),
                    118, 76, 68, 36.4, 97, 62.0, 165.0, 22.8, 92),
                
                // Michael Johnson - high BP, overweight (cholesterol + obesity)
                createVitalSigns(patients.get(2), LocalDateTime.now().minusDays(5),
                    152, 95, 82, 36.9, 96, 98.0, 178.0, 30.9, 108)
            );

            vitalSigns = vitalSignsRepository.saveAll(vitalSigns);
            log.info("✅ Created {} vital signs records", vitalSigns.size());

            // ================================================================
            // STEP 3: CREATE CONSULTATIONS
            // ================================================================
            log.info("\n🩺 Step 3: Creating Consultations...");
            
            List<Consultation> consultations = Arrays.asList(
                // John Doe - diabetes checkup
                createConsultation(patients.get(0), LocalDateTime.now().minusDays(7),
                    "Routine diabetes checkup",
                    "Patient reports occasional dizziness and increased thirst",
                    "Type 2 Diabetes Mellitus with suboptimal control. Hypertension Stage 1.",
                    "Adjust Metformin dosage. Continue ACE inhibitor.",
                    "Metformin 1000mg twice daily, Lisinopril 10mg once daily",
                    "COMPLETED"
                ),
                
                // Jane Smith - asthma follow-up
                createConsultation(patients.get(1), LocalDateTime.now().minusDays(10),
                    "Asthma follow-up and inhaler refill",
                    "Mild shortness of breath during exercise, no recent attacks",
                    "Mild persistent asthma, well-controlled",
                    "Continue current asthma management. Inhaler refill provided.",
                    "Albuterol inhaler PRN, Fluticasone 110mcg twice daily",
                    "COMPLETED"
                ),
                
                // Michael Johnson - high cholesterol
                createConsultation(patients.get(2), LocalDateTime.now().minusDays(5),
                    "New patient consultation - high cholesterol",
                    "Family history of heart disease. No chest pain. Sedentary lifestyle.",
                    "Hypercholesterolemia. Obesity (BMI 30.9). Cardiovascular risk factors.",
                    "Initiate statin therapy. Recommend diet and exercise program.",
                    "Atorvastatin 20mg once daily at bedtime",
                    "COMPLETED"
                )
            );

            consultations = consultationRepository.saveAll(consultations);
            log.info("✅ Created {} consultations", consultations.size());

            // ================================================================
            // STEP 4: CREATE TREATMENTS
            // ================================================================
            log.info("\n💊 Step 4: Creating Treatments...");
            
            List<Treatment> treatments = Arrays.asList(
                // John Doe - Diabetes medication
                createTreatment(patients.get(0), "Metformin", "1000mg", "Twice daily", "ACTIVE",
                    LocalDate.now().minusYears(2), LocalDate.now().plusYears(1)),
                
                // John Doe - Hypertension medication
                createTreatment(patients.get(0), "Lisinopril", "10mg", "Once daily", "ACTIVE",
                    LocalDate.now().minusYears(1), LocalDate.now().plusYears(1)),
                
                // Jane Smith - Asthma controller
                createTreatment(patients.get(1), "Fluticasone", "110mcg", "Twice daily", "ACTIVE",
                    LocalDate.now().minusMonths(6), LocalDate.now().plusYears(1)),
                
                // Jane Smith - Rescue inhaler
                createTreatment(patients.get(1), "Albuterol", "90mcg", "As needed", "ACTIVE",
                    LocalDate.now().minusMonths(6), LocalDate.now().plusYears(1)),
                
                // Michael Johnson - Cholesterol medication
                createTreatment(patients.get(2), "Atorvastatin", "20mg", "Once daily at bedtime", "ACTIVE",
                    LocalDate.now().minusDays(5), LocalDate.now().plusYears(1))
            );

            treatments = treatmentRepository.saveAll(treatments);
            log.info("✅ Created {} treatments", treatments.size());

            // ================================================================
            // STEP 5: CREATE MEDICAL ANALYSES
            // ================================================================
            log.info("\n🔬 Step 5: Creating Medical Analyses...");
            
            List<MedicalAnalysis> analyses = Arrays.asList(
                // John Doe - Blood test with abnormal glucose
                createMedicalAnalysis(patients.get(0), "blood_test_john_doe_2024.pdf",
                    "Complete Blood Count + Metabolic Panel",
                    LocalDate.now().minusDays(14),
                    "Blood glucose: 145 mg/dL, HbA1c: 7.2%, Cholesterol: 210 mg/dL",
                    "Elevated blood glucose (fasting: 145 mg/dL, normal <100). HbA1c slightly elevated.",
                    "Monitor glucose levels closely. Consider medication adjustment.",
                    "Blood glucose elevated - diabetes control needed"
                ),
                
                // Jane Smith - Normal lung function test
                createMedicalAnalysis(patients.get(1), "spirometry_jane_smith.pdf",
                    "Pulmonary Function Test",
                    LocalDate.now().minusDays(20),
                    "FEV1: 95% predicted, FVC: 98% predicted, FEV1/FVC ratio: 0.82",
                    "Normal spirometry results. No significant airflow obstruction.",
                    "Continue current asthma management plan.",
                    "Pulmonary function within normal limits"
                ),
                
                // Michael Johnson - Lipid panel (abnormal)
                createMedicalAnalysis(patients.get(2), "lipid_panel_michael_j.pdf",
                    "Lipid Panel",
                    LocalDate.now().minusDays(16),
                    "Total Cholesterol: 285 mg/dL, LDL: 185 mg/dL, HDL: 35 mg/dL, Triglycerides: 220 mg/dL",
                    "Significantly elevated LDL cholesterol and low HDL. High cardiovascular risk.",
                    "Initiate statin therapy. Lifestyle modifications mandatory. Repeat in 3 months.",
                    "High LDL cholesterol - cardiovascular risk"
                )
            );

            analyses = medicalAnalysisRepository.saveAll(analyses);
            log.info("✅ Created {} medical analyses", analyses.size());

            // ================================================================
            // STEP 6: CREATE CARDIOVASCULAR EXAMS
            // ================================================================
            log.info("\n❤️  Step 6: Creating Cardiovascular Exams...");
            
            List<CardiovascularExam> cardioExams = Arrays.asList(
                // John Doe - ECG with minor findings
                createCardiovascularExam(patients.get(0), LocalDateTime.now().minusDays(12),
                    "ECG - 12 Lead",
                    "Heart Rate: 78 bpm, Blood Pressure: 145/92 mmHg, QRS Duration: 95ms, QT Interval: 410ms",
                    "Sinus rhythm. Mild left ventricular hypertrophy. No acute ischemic changes.",
                    "Mild left ventricular hypertrophy noted"
                ),
                
                // Michael Johnson - Echocardiogram
                createCardiovascularExam(patients.get(2), LocalDateTime.now().minusDays(18),
                    "Echocardiogram",
                    "Heart Rate: 75 bpm, Blood Pressure: 138/88 mmHg, LVEF: 55%, LA size: Normal, LV size: Normal",
                    "Left ventricular ejection fraction 55%. Mild mitral regurgitation. No wall motion abnormalities.",
                    "Mild mitral regurgitation"
                )
            );

            cardioExams = cardiovascularExamRepository.saveAll(cardioExams);
            log.info("✅ Created {} cardiovascular exams", cardioExams.size());

            // ================================================================
            // SUMMARY
            // ================================================================
            log.info("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("  ✨ Database Seeding Complete!");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("📊 Summary:");
            log.info("   • Patients: {}", patients.size());
            log.info("   • Vital Signs: {}", vitalSigns.size());
            log.info("   • Consultations: {}", consultations.size());
            log.info("   • Treatments: {}", treatments.size());
            log.info("   • Medical Analyses: {}", analyses.size());
            log.info("   • Cardiovascular Exams: {}", cardioExams.size());
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        };
    }

    // ================================================================
    // HELPER METHODS
    // ================================================================

    private Patient createPatient(String firstName, String lastName, LocalDate dob, String gender,
                                 String phone, String email, String address, String city, 
                                 String postalCode, String country, String bloodType, 
                                 String allergies, String chronicDiseases, String treatingPhysician,
                                 String emergencyContact, UUID createdBy) {
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
        patient.setBloodGroup(bloodType);
        patient.setAllergies(allergies);
        patient.setChronicDiseases(chronicDiseases);
        patient.setAttendingPhysician(treatingPhysician);
        patient.setNotes(emergencyContact);
        patient.setCreatedBy(createdBy);
        patient.setActive(true);
        return patient;
    }

    private VitalSigns createVitalSigns(Patient patient, LocalDateTime recordDate,
                                       int systolic, int diastolic, int heartRate,
                                       double temperature, int oxygenSaturation,
                                       double weight, double height, double bmi,
                                       int glucoseLevel) {
        VitalSigns vs = new VitalSigns();
        vs.setPatient(patient);
        vs.setMeasurementDate(recordDate);
        vs.setSystolicBP(systolic);
        vs.setDiastolicBP(diastolic);
        vs.setHeartRate(heartRate);
        vs.setTemperature(temperature);
        vs.setOxygenSaturation(oxygenSaturation);
        vs.setWeight(weight);
        vs.setHeight(height);
        vs.setBmi(bmi);
        vs.setBloodGlucose((double) glucoseLevel);
        return vs;
    }

    private Consultation createConsultation(Patient patient, LocalDateTime consultationDate,
                                          String reasonForVisit, String symptoms,
                                          String diagnosis, String treatment,
                                          String prescriptions, String status) {
        Consultation consultation = new Consultation();
        consultation.setPatient(patient);
        consultation.setConsultationDate(consultationDate);
        consultation.setReasonForVisit(reasonForVisit);
        consultation.setSymptoms(symptoms);
        consultation.setDiagnosis(diagnosis);
        consultation.setTreatment(treatment);
        consultation.setPrescriptions(prescriptions);
        consultation.setStatus(status);
        return consultation;
    }

    private Treatment createTreatment(Patient patient, String medicationName,
                                     String dosage, String frequency, String status,
                                     LocalDate startDate, LocalDate endDate) {
        Treatment treatment = new Treatment();
        treatment.setPatient(patient);
        treatment.setMedicationName(medicationName);
        treatment.setDosage(dosage);
        treatment.setFrequency(frequency);
        treatment.setStatus(status);
        treatment.setStartDate(startDate);
        treatment.setEndDate(endDate);
        return treatment;
    }

    private MedicalAnalysis createMedicalAnalysis(Patient patient, String fileName,
                                                 String analysisType, LocalDate analysisDate,
                                                 String results, String interpretation,
                                                 String recommendations, String alerts) {
        MedicalAnalysis analysis = new MedicalAnalysis();
        analysis.setPatient(patient);
        analysis.setFileName(fileName);
        analysis.setAnalysisType(analysisType);
        analysis.setAnalysisDate(analysisDate);
        analysis.setResults(results);
        analysis.setInterpretation(interpretation);
        analysis.setRecommendations(recommendations);
        analysis.setAlertsAndAnomalies(alerts);
        analysis.setStatus("COMPLETED");
        return analysis;
    }

    private CardiovascularExam createCardiovascularExam(Patient patient, LocalDateTime examDate,
                                                       String examType, String results,
                                                       String interpretation, String abnormalities) {
        CardiovascularExam exam = new CardiovascularExam();
        exam.setPatient(patient);
        exam.setExamDate(examDate);
        exam.setExamType(examType);
        exam.setResults(results);
        exam.setInterpretation(interpretation);
        exam.setAbnormalities(abnormalities);
        exam.setStatus("COMPLETED");
        return exam;
    }
}
