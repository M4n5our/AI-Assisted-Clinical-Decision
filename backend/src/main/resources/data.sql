-- Seed data: sample clinical assessments for demo purposes (synthetic data only)
-- This runs automatically on startup when using the H2 profile

MERGE INTO suggestion (id, age, systolic_bp, cholesterol, glucose, bmi, risk_score, risk_level, explanation, created_at) KEY(id) VALUES
(1, 72, 165, 280, 145, 33.5, 1.0, 'High',
 'Risk factors identified: Age > 60 (+0.2), Systolic BP > 140 (+0.2), Cholesterol > 240 (+0.2), Glucose > 126 (+0.2), BMI > 30 (+0.2). Total score capped at 1.0.',
 TIMESTAMP '2026-02-14 09:15:00');

MERGE INTO suggestion (id, age, systolic_bp, cholesterol, glucose, bmi, risk_score, risk_level, explanation, created_at) KEY(id) VALUES
(2, 45, 128, 210, 95, 26.0, 0.0, 'Low',
 'All parameters are within normal ranges. No significant risk factors identified.',
 TIMESTAMP '2026-02-14 11:30:00');

MERGE INTO suggestion (id, age, systolic_bp, cholesterol, glucose, bmi, risk_score, risk_level, explanation, created_at) KEY(id) VALUES
(3, 63, 155, 260, 100, 28.0, 0.6, 'Moderate',
 'Risk factors identified: Age > 60 (+0.2), Systolic BP > 140 (+0.2), Cholesterol > 240 (+0.2). Moderate risk — lifestyle changes recommended.',
 TIMESTAMP '2026-02-15 08:45:00');
