CREATE DATABASE IF NOT EXISTS cdss_db;
USE cdss_db;

CREATE TABLE IF NOT EXISTS suggestion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    age INT NOT NULL,
    systolic_bp INT NOT NULL,
    cholesterol INT NOT NULL,
    glucose INT NOT NULL,
    bmi DOUBLE NOT NULL,
    risk_score DOUBLE NOT NULL,
    risk_level VARCHAR(50) NOT NULL,
    explanation TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
