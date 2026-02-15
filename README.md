# AI-Assisted Clinical Decision Support System (CDSS) — MVP

A working prototype of a clinical decision support system with rule-based risk scoring, structured patient input, and full end-to-end integration across four layers.

## Architecture

```
React Frontend (port 3000)
        ↓
Spring Boot Backend (port 8080)
        ↓
Python FastAPI Model Service (port 8000)
        ↓
MySQL Database (port 3306)
```

## Prerequisites

- **Java 17** (Gradle wrapper included)
- **Python 3.10+**
- **Node.js 18+** and **npm**
- **MySQL 8** (or use the H2 in-memory profile for quick testing)

## Quick Start

### 1. Database Setup

**Option A — MySQL:**

```bash
mysql -u root -p < db/init.sql
```

Then verify `application.properties` in `backend/src/main/resources/` has the correct MySQL credentials.

**Option B — H2 (no MySQL needed):**

Run the Spring Boot backend with the `h2` profile (see step 3).

### 2. Start the Model Service

```bash
cd model-service
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8000
```

Verify: `curl http://localhost:8000/health` should return `{"status":"ok"}`.

### 3. Start the Backend

```bash
cd backend

# With MySQL:
./gradlew bootRun

# With H2 (no MySQL needed):
./gradlew bootRun --args='--spring.profiles.active=h2'
```

Verify: `curl http://localhost:8080/api/health` should return `Backend is running`.

### 4. Start the Frontend

```bash
cd frontend
npm install
npm start
```

Opens at `http://localhost:3000`.

## Testing the Pipeline

Use the demo scenario from the spec:

| Field | Value |
|---|---|
| Age | 65 |
| Systolic BP | 150 |
| Cholesterol | 250 |
| Glucose | 130 |
| BMI | 32 |

Expected output:
- **Risk Score:** 1.0
- **Risk Level:** High
- **Explanation:** Lists all five contributing factors

You can also test the model service directly:

```bash
curl -X POST http://localhost:8000/predict \
  -H "Content-Type: application/json" \
  -d '{"age":65,"systolic_bp":150,"cholesterol":250,"glucose":130,"bmi":32.5}'
```

## Project Structure

```
├── model-service/          # Python FastAPI — risk scoring engine
│   ├── main.py
│   └── requirements.txt
├── backend/                # Spring Boot — REST API, JPA, MySQL
│   ├── build.gradle
│   └── src/main/java/com/cdss/
│       ├── CdssApplication.java
│       ├── config/WebConfig.java
│       ├── controller/CdssController.java
│       ├── dto/PatientRequest.java
│       ├── dto/PredictionResponse.java
│       ├── model/Suggestion.java
│       ├── repository/SuggestionRepository.java
│       └── service/CdssService.java
├── frontend/               # React — patient input form + results
│   ├── package.json
│   ├── public/index.html
│   └── src/
│       ├── index.js
│       ├── App.js
│       └── App.css
├── db/
│   └── init.sql            # MySQL schema
└── README.md
```

## Risk Scoring Logic

| Condition | Score |
|---|---|
| Age > 60 | +0.2 |
| Systolic BP > 140 | +0.2 |
| Cholesterol > 240 | +0.2 |
| Glucose > 126 | +0.2 |
| BMI > 30 | +0.2 |

**Risk levels:** Low (0–0.29), Moderate (0.30–0.69), High (0.70–1.0)

## API Reference

### Model Service

`POST /predict` — Calculate risk from patient data
`GET /health` — Health check

### Backend

`POST /api/generate` — Full pipeline: score + store + return
`GET /api/health` — Health check
