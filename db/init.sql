CREATE DATABASE IF NOT EXISTS cdss_db;
USE cdss_db;

CREATE TABLE IF NOT EXISTS suggestion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    age INT NOT NULL CHECK (age >= 0 AND age <= 150),
    systolic_bp INT NOT NULL CHECK (systolic_bp >= 50 AND systolic_bp <= 300),
    cholesterol INT NOT NULL CHECK (cholesterol >= 50 AND cholesterol <= 500),
    glucose INT NOT NULL CHECK (glucose >= 20 AND glucose <= 500),
    bmi DOUBLE NOT NULL CHECK (bmi >= 10.0 AND bmi <= 80.0),
    risk_score DOUBLE NOT NULL CHECK (risk_score >= 0.0 AND risk_score <= 1.0),
    risk_level VARCHAR(50) NOT NULL,
    explanation TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_created_at (created_at),
    INDEX idx_risk_level (risk_level)
);

-- Seed data: synthetic clinical assessments for demo purposes
INSERT IGNORE INTO suggestion (id, age, systolic_bp, cholesterol, glucose, bmi, risk_score, risk_level, explanation, created_at) VALUES
(1, 72, 165, 280, 145, 33.5, 1.0, 'High',
 'Risk factors identified: Age > 60 (+0.2), Systolic BP > 140 (+0.2), Cholesterol > 240 (+0.2), Glucose > 126 (+0.2), BMI > 30 (+0.2). Total score capped at 1.0.',
 '2026-02-14 09:15:00'),
(2, 45, 128, 210, 95, 26.0, 0.0, 'Low',
 'All parameters are within normal ranges. No significant risk factors identified.',
 '2026-02-14 11:30:00'),
(3, 63, 155, 260, 100, 28.0, 0.6, 'Moderate',
 'Risk factors identified: Age > 60 (+0.2), Systolic BP > 140 (+0.2), Cholesterol > 240 (+0.2). Moderate risk — lifestyle changes recommended.',
 '2026-02-15 08:45:00');
