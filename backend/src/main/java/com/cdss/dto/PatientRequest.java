package com.cdss.dto;

import jakarta.validation.constraints.*;

public class PatientRequest {

    @NotNull
    @Min(0) @Max(150)
    private Integer age;

    @NotNull
    @Min(50) @Max(300)
    private Integer systolicBp;

    @NotNull
    @Min(50) @Max(500)
    private Integer cholesterol;

    @NotNull
    @Min(20) @Max(500)
    private Integer glucose;

    @NotNull
    @DecimalMin("10.0") @DecimalMax("80.0")
    private Double bmi;

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Integer getSystolicBp() { return systolicBp; }
    public void setSystolicBp(Integer systolicBp) { this.systolicBp = systolicBp; }

    public Integer getCholesterol() { return cholesterol; }
    public void setCholesterol(Integer cholesterol) { this.cholesterol = cholesterol; }

    public Integer getGlucose() { return glucose; }
    public void setGlucose(Integer glucose) { this.glucose = glucose; }

    public Double getBmi() { return bmi; }
    public void setBmi(Double bmi) { this.bmi = bmi; }
}
