import pytest
from fastapi.testclient import TestClient
from main import app, calculate_risk, PatientInput

client = TestClient(app)


class TestCalculateRisk:
    """Tests for the core risk calculation logic."""

    def test_all_normal_values_returns_low(self):
        patient = PatientInput(
            age=30, systolic_bp=120, cholesterol=200, glucose=90, bmi=22.0
        )
        result = calculate_risk(patient)
        assert result.risk_score == 0.0
        assert result.risk_level == "Low"
        assert "within normal" in result.explanation

    def test_all_elevated_values_returns_high(self):
        patient = PatientInput(
            age=65, systolic_bp=150, cholesterol=250, glucose=130, bmi=32.0
        )
        result = calculate_risk(patient)
        assert result.risk_score == 1.0
        assert result.risk_level == "High"

    def test_single_factor_elevated_returns_low(self):
        patient = PatientInput(
            age=65, systolic_bp=120, cholesterol=200, glucose=90, bmi=22.0
        )
        result = calculate_risk(patient)
        assert result.risk_score == 0.2
        assert result.risk_level == "Low"
        assert "Age" in result.explanation

    def test_three_factors_returns_moderate(self):
        patient = PatientInput(
            age=65, systolic_bp=150, cholesterol=250, glucose=90, bmi=22.0
        )
        result = calculate_risk(patient)
        assert result.risk_score == 0.6
        assert result.risk_level == "Moderate"

    def test_boundary_values_not_elevated(self):
        """Values exactly at threshold should NOT trigger risk."""
        patient = PatientInput(
            age=60, systolic_bp=140, cholesterol=240, glucose=126, bmi=30.0
        )
        result = calculate_risk(patient)
        assert result.risk_score == 0.0
        assert result.risk_level == "Low"

    def test_boundary_values_just_above(self):
        """Values just above threshold should trigger risk."""
        patient = PatientInput(
            age=61, systolic_bp=141, cholesterol=241, glucose=127, bmi=30.1
        )
        result = calculate_risk(patient)
        assert result.risk_score == 1.0
        assert result.risk_level == "High"


class TestPredictEndpoint:
    """Tests for the /predict API endpoint."""

    def test_predict_valid_input(self):
        response = client.post("/predict", json={
            "age": 65,
            "systolic_bp": 150,
            "cholesterol": 250,
            "glucose": 130,
            "bmi": 32.0,
        })
        assert response.status_code == 200
        data = response.json()
        assert data["risk_score"] == 1.0
        assert data["risk_level"] == "High"
        assert "explanation" in data

    def test_predict_missing_field_returns_422(self):
        response = client.post("/predict", json={
            "age": 65,
            "systolic_bp": 150,
        })
        assert response.status_code == 422

    def test_predict_out_of_range_returns_422(self):
        response = client.post("/predict", json={
            "age": -1,
            "systolic_bp": 150,
            "cholesterol": 250,
            "glucose": 130,
            "bmi": 32.0,
        })
        assert response.status_code == 422


class TestHealthEndpoint:
    def test_health_returns_ok(self):
        response = client.get("/health")
        assert response.status_code == 200
        assert response.json() == {"status": "ok"}
