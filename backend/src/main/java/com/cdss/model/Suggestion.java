package com.cdss.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suggestion", indexes = {
    @Index(name = "idx_created_at", columnList = "created_at"),
    @Index(name = "idx_risk_level", columnList = "risk_level")
})
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int age;

    @Column(name = "systolic_bp", nullable = false)
    private int systolicBp;

    @Column(nullable = false)
    private int cholesterol;

    @Column(nullable = false)
    private int glucose;

    @Column(nullable = false)
    private double bmi;

    @Column(name = "risk_score", nullable = false)
    private double riskScore;

    @Column(name = "risk_level", nullable = false, length = 50)
    private String riskLevel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Suggestion() {}

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public int getSystolicBp() { return systolicBp; }
    public void setSystolicBp(int systolicBp) { this.systolicBp = systolicBp; }

    public int getCholesterol() { return cholesterol; }
    public void setCholesterol(int cholesterol) { this.cholesterol = cholesterol; }

    public int getGlucose() { return glucose; }
    public void setGlucose(int glucose) { this.glucose = glucose; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
