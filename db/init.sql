CREATE DATABASE IF NOT EXISTS cdss_db;
USE cdss_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patients table
CREATE TABLE IF NOT EXISTS patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Encounters table
CREATE TABLE IF NOT EXISTS encounters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Observations table
CREATE TABLE IF NOT EXISTS observations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    encounter_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    value DOUBLE NOT NULL,
    unit VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (encounter_id) REFERENCES encounters(id)
);

-- Templates table
CREATE TABLE IF NOT EXISTS templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Suggestions table
CREATE TABLE IF NOT EXISTS suggestions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    encounter_id BIGINT NOT NULL,
    risk_score DOUBLE NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    explanation TEXT NOT NULL,
    template_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (encounter_id) REFERENCES encounters(id),
    FOREIGN KEY (template_id) REFERENCES templates(id)
);

-- Feedbacks table
CREATE TABLE IF NOT EXISTS feedbacks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    suggestion_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (suggestion_id) REFERENCES suggestions(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
