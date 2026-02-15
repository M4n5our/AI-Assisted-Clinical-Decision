import React, { useState } from 'react';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

const INITIAL_FORM = {
  age: '',
  systolicBp: '',
  cholesterol: '',
  glucose: '',
  bmi: '',
};

function App() {
  const [form, setForm] = useState(INITIAL_FORM);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});
  const [history, setHistory] = useState([]);
  const [showHistory, setShowHistory] = useState(false);
  const [historyLoading, setHistoryLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    if (fieldErrors[e.target.name]) {
      setFieldErrors({ ...fieldErrors, [e.target.name]: '' });
    }
  };

  const validateForm = () => {
    const errors = {};

    if (form.age === '') {
      errors.age = 'Age is required';
    } else if (isNaN(Number(form.age)) || !Number.isInteger(Number(form.age))) {
      errors.age = 'Age must be a whole number';
    } else {
      const age = parseInt(form.age, 10);
      if (age < 0 || age > 150) {
        errors.age = 'Age must be between 0 and 150';
      }
    }

    if (form.systolicBp === '') {
      errors.systolicBp = 'Systolic BP is required';
    } else if (isNaN(Number(form.systolicBp)) || !Number.isInteger(Number(form.systolicBp))) {
      errors.systolicBp = 'Systolic BP must be a whole number';
    } else {
      const bp = parseInt(form.systolicBp, 10);
      if (bp < 50 || bp > 300) {
        errors.systolicBp = 'Systolic BP must be between 50 and 300';
      }
    }

    if (form.cholesterol === '') {
      errors.cholesterol = 'Cholesterol is required';
    } else if (isNaN(Number(form.cholesterol)) || !Number.isInteger(Number(form.cholesterol))) {
      errors.cholesterol = 'Cholesterol must be a whole number';
    } else {
      const chol = parseInt(form.cholesterol, 10);
      if (chol < 50 || chol > 500) {
        errors.cholesterol = 'Cholesterol must be between 50 and 500';
      }
    }

    if (form.glucose === '') {
      errors.glucose = 'Glucose is required';
    } else if (isNaN(Number(form.glucose)) || !Number.isInteger(Number(form.glucose))) {
      errors.glucose = 'Glucose must be a whole number';
    } else {
      const glu = parseInt(form.glucose, 10);
      if (glu < 20 || glu > 500) {
        errors.glucose = 'Glucose must be between 20 and 500';
      }
    }

    if (form.bmi === '') {
      errors.bmi = 'BMI is required';
    } else if (isNaN(Number(form.bmi))) {
      errors.bmi = 'BMI must be a number';
    } else {
      const bmi = parseFloat(form.bmi);
      if (bmi < 10 || bmi > 80) {
        errors.bmi = 'BMI must be between 10.0 and 80.0';
      }
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setResult(null);

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    const payload = {
      age: parseInt(form.age, 10),
      systolicBp: parseInt(form.systolicBp, 10),
      cholesterol: parseInt(form.cholesterol, 10),
      glucose: parseInt(form.glucose, 10),
      bmi: parseFloat(form.bmi),
    };

    try {
      const response = await fetch(`${API_URL}/api/generate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Server error: ${response.status}`);
      }

      const data = await response.json();
      setResult(data);

      if (showHistory) {
        fetchHistory();
      }
    } catch (err) {
      if (err.message === 'Failed to fetch') {
        setError('Cannot connect to backend. Make sure the Spring Boot server is running on port 8080.');
      } else {
        setError(err.message || 'Failed to connect to backend');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setForm(INITIAL_FORM);
    setResult(null);
    setError('');
    setFieldErrors({});
  };

  const fetchHistory = async () => {
    setHistoryLoading(true);
    setError('');
    try {
      const response = await fetch(`${API_URL}/api/history`);
      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Server error: ${response.status}`);
      }
      const data = await response.json();
      setHistory(data);
      setShowHistory(true);
    } catch (err) {
      if (err.message === 'Failed to fetch') {
        setError('Cannot connect to backend. Make sure the Spring Boot server is running on port 8080.');
      } else {
        setError(err.message || 'Failed to load history');
      }
    } finally {
      setHistoryLoading(false);
    }
  };

  const toggleHistory = () => {
    if (!showHistory) {
      fetchHistory();
    } else {
      setShowHistory(false);
    }
  };

  const riskLevelClass = result
    ? `risk-badge risk-${result.riskLevel.toLowerCase()}`
    : '';

  return (
    <div className="app">
      <header className="header">
        <div className="header-icon">&#9877;</div>
        <h1>Clinical Decision Support System</h1>
        <p>AI-Assisted Risk Assessment Tool</p>
      </header>

      <main className="main">
        <section className="form-section">
          <h2>Patient Data Input</h2>
          <form onSubmit={handleSubmit} noValidate>
            <div className="form-group">
              <label htmlFor="age">Age (years)</label>
              <input
                id="age"
                name="age"
                type="number"
                min="0"
                max="150"
                value={form.age}
                onChange={handleChange}
                placeholder="e.g. 65"
                className={fieldErrors.age ? 'input-error' : ''}
              />
              {fieldErrors.age && <span className="field-error">{fieldErrors.age}</span>}
            </div>
            <div className="form-group">
              <label htmlFor="systolicBp">Systolic Blood Pressure (mmHg)</label>
              <input
                id="systolicBp"
                name="systolicBp"
                type="number"
                min="50"
                max="300"
                value={form.systolicBp}
                onChange={handleChange}
                placeholder="e.g. 150"
                className={fieldErrors.systolicBp ? 'input-error' : ''}
              />
              {fieldErrors.systolicBp && <span className="field-error">{fieldErrors.systolicBp}</span>}
            </div>
            <div className="form-group">
              <label htmlFor="cholesterol">Cholesterol (mg/dL)</label>
              <input
                id="cholesterol"
                name="cholesterol"
                type="number"
                min="50"
                max="500"
                value={form.cholesterol}
                onChange={handleChange}
                placeholder="e.g. 250"
                className={fieldErrors.cholesterol ? 'input-error' : ''}
              />
              {fieldErrors.cholesterol && <span className="field-error">{fieldErrors.cholesterol}</span>}
            </div>
            <div className="form-group">
              <label htmlFor="glucose">Glucose (mg/dL)</label>
              <input
                id="glucose"
                name="glucose"
                type="number"
                min="20"
                max="500"
                value={form.glucose}
                onChange={handleChange}
                placeholder="e.g. 130"
                className={fieldErrors.glucose ? 'input-error' : ''}
              />
              {fieldErrors.glucose && <span className="field-error">{fieldErrors.glucose}</span>}
            </div>
            <div className="form-group">
              <label htmlFor="bmi">BMI</label>
              <input
                id="bmi"
                name="bmi"
                type="number"
                step="0.1"
                min="10"
                max="80"
                value={form.bmi}
                onChange={handleChange}
                placeholder="e.g. 32.5"
                className={fieldErrors.bmi ? 'input-error' : ''}
              />
              {fieldErrors.bmi && <span className="field-error">{fieldErrors.bmi}</span>}
            </div>
            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Analyzing...' : 'Generate Risk Assessment'}
              </button>
              <button type="button" className="btn btn-secondary" onClick={handleReset}>
                Reset
              </button>
            </div>
          </form>
        </section>

        {error && (
          <section className="error-section">
            <p>{error}</p>
          </section>
        )}

        {result && (
          <section className="result-section">
            <h2>Risk Assessment Result</h2>
            <div className="result-card">
              <div className="risk-score-display">
                <div className="risk-score-number">
                  {(result.riskScore * 100).toFixed(0)}%
                </div>
                <span className={riskLevelClass}>{result.riskLevel}</span>
              </div>
              <div className="risk-bar-container">
                <div
                  className={`risk-bar risk-bar-${result.riskLevel.toLowerCase()}`}
                  style={{ width: `${result.riskScore * 100}%` }}
                />
              </div>
              <div className="result-row">
                <span className="result-label">Risk Score</span>
                <span className="result-value">{result.riskScore}</span>
              </div>
              <div className="result-row explanation">
                <span className="result-label">Explanation</span>
                <p className="result-value">{result.explanation}</p>
              </div>
            </div>
          </section>
        )}

        <section className="history-section">
          <button
            type="button"
            className="btn btn-secondary btn-full"
            onClick={toggleHistory}
            disabled={historyLoading}
          >
            {historyLoading
              ? 'Loading...'
              : showHistory
              ? 'Hide Assessment History'
              : 'View Assessment History'}
          </button>

          {showHistory && (
            <div className="history-list">
              {history.length === 0 ? (
                <p className="history-empty">No past assessments found.</p>
              ) : (
                history.map((item) => (
                  <div key={item.id} className="history-card">
                    <div className="history-header">
                      <span className={`risk-badge risk-${item.riskLevel.toLowerCase()}`}>
                        {item.riskLevel}
                      </span>
                      <span className="history-date">
                        {new Date(item.createdAt).toLocaleString()}
                      </span>
                    </div>
                    <div className="history-details">
                      <span>Age: {item.age}</span>
                      <span>BP: {item.systolicBp}</span>
                      <span>Chol: {item.cholesterol}</span>
                      <span>Glu: {item.glucose}</span>
                      <span>BMI: {item.bmi}</span>
                    </div>
                    <div className="history-score">
                      Risk Score: {item.riskScore}
                    </div>
                    <p className="history-explanation">{item.explanation}</p>
                  </div>
                ))
              )}
            </div>
          )}
        </section>
      </main>

      <footer className="footer">
        <p>CDSS MVP &mdash; For demonstration purposes only. Not for clinical use.</p>
      </footer>
    </div>
  );
}

export default App;
