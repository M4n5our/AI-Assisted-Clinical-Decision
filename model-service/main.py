from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

app = FastAPI(title="CDSS Model Service", version="1.0.0")


class PatientInput(BaseModel):
    age: int = Field(..., ge=0, le=150)
    systolic_bp: int = Field(..., ge=50, le=300)
    cholesterol: int = Field(..., ge=50, le=500)
    glucose: int = Field(..., ge=20, le=500)
    bmi: float = Field(..., ge=10.0, le=80.0)


class PredictionOutput(BaseModel):
    risk_score: float
    risk_level: str
    explanation: str


def calculate_risk(patient: PatientInput) -> PredictionOutput:
    score = 0.0
    factors = []

    if patient.age > 60:
        score += 0.2
        factors.append(f"Age ({patient.age}) exceeds 60")
    if patient.systolic_bp > 140:
        score += 0.2
        factors.append(f"Systolic BP ({patient.systolic_bp}) exceeds 140 mmHg")
    if patient.cholesterol > 240:
        score += 0.2
        factors.append(f"Cholesterol ({patient.cholesterol}) exceeds 240 mg/dL")
    if patient.glucose > 126:
        score += 0.2
        factors.append(f"Glucose ({patient.glucose}) exceeds 126 mg/dL")
    if patient.bmi > 30:
        score += 0.2
        factors.append(f"BMI ({patient.bmi}) exceeds 30")

    score = round(score, 2)

    if score >= 0.7:
        risk_level = "High"
    elif score >= 0.3:
        risk_level = "Moderate"
    else:
        risk_level = "Low"

    if factors:
        explanation = (
            f"Risk score {score} ({risk_level}). "
            f"Contributing factors: {'; '.join(factors)}."
        )
    else:
        explanation = (
            f"Risk score {score} ({risk_level}). "
            "All values are within normal clinical thresholds."
        )

    return PredictionOutput(
        risk_score=score, risk_level=risk_level, explanation=explanation
    )


@app.post("/predict", response_model=PredictionOutput)
def predict(patient: PatientInput):
    return calculate_risk(patient)


@app.get("/health")
def health():
    return {"status": "ok"}
