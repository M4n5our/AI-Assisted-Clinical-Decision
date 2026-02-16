package com.cdss.config;

import com.cdss.model.*;
import com.cdss.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final TemplateRepository templateRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      PatientRepository patientRepository,
                      TemplateRepository templateRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.templateRepository = templateRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUsers();
        }
        if (patientRepository.count() == 0) {
            seedPatients();
        }
        if (templateRepository.count() == 0) {
            seedTemplates();
        }
    }

    private void seedUsers() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("System Administrator");
        admin.setEmail("admin@cdss.com");
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);

        User doctor = new User();
        doctor.setUsername("doctor");
        doctor.setPassword(passwordEncoder.encode("doctor123"));
        doctor.setFullName("Dr. Sarah Johnson");
        doctor.setEmail("sarah.johnson@cdss.com");
        doctor.setRole(User.Role.USER);
        userRepository.save(doctor);
    }

    private void seedPatients() {
        Patient p1 = new Patient();
        p1.setFirstName("John");
        p1.setLastName("Smith");
        p1.setDateOfBirth(LocalDate.of(1954, 3, 15));
        p1.setGender(Patient.Gender.MALE);
        p1.setPhone("555-0101");
        p1.setEmail("john.smith@example.com");
        patientRepository.save(p1);

        Patient p2 = new Patient();
        p2.setFirstName("Maria");
        p2.setLastName("Garcia");
        p2.setDateOfBirth(LocalDate.of(1980, 7, 22));
        p2.setGender(Patient.Gender.FEMALE);
        p2.setPhone("555-0102");
        p2.setEmail("maria.garcia@example.com");
        patientRepository.save(p2);

        Patient p3 = new Patient();
        p3.setFirstName("Robert");
        p3.setLastName("Williams");
        p3.setDateOfBirth(LocalDate.of(1962, 11, 8));
        p3.setGender(Patient.Gender.MALE);
        p3.setPhone("555-0103");
        p3.setEmail("robert.williams@example.com");
        patientRepository.save(p3);
    }

    private void seedTemplates() {
        Template t1 = new Template();
        t1.setName("Cardiovascular Risk Assessment");
        t1.setContent("Evaluate cardiovascular risk factors including blood pressure, cholesterol, glucose, BMI, and age. Recommend lifestyle modifications and pharmacological interventions as indicated.");
        t1.setCategory("cardiovascular");
        templateRepository.save(t1);

        Template t2 = new Template();
        t2.setName("Diabetes Screening");
        t2.setContent("Screen for diabetes risk using fasting glucose levels, BMI, and family history. Recommend HbA1c testing if glucose is elevated.");
        t2.setCategory("endocrine");
        templateRepository.save(t2);

        Template t3 = new Template();
        t3.setName("General Health Check");
        t3.setContent("Comprehensive health assessment including vital signs, basic metabolic panel, and risk factor analysis for common chronic conditions.");
        t3.setCategory("general");
        templateRepository.save(t3);
    }
}
