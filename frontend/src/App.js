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

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setResult(null);
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
    } catch (err) {
      setError(err.message || 'Failed to connect to backend');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setForm(INITIAL_FORM);
    setResult(null);
    setError('');
  };

  const riskLevelClass = result
    ? `risk-badge risk-${result.riskLevel.toLowerCase()}`
    : '';

  return (
    <div className="app">
      <header className="header">
        <h1>Clinical Decision Support System</h1>
        <p>AI-Assisted Risk Assessment Tool</p>
      </header>

      <main className="main">
        <section className="form-section">
          <h2>Patient Data Input</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="age">Age (years)</label>
              <input
                id="age"
                name="age"
                type="number"
                min="0"
                max="150"
                required
                value={form.age}
                onChange={handleChange}
                placeholder="e.g. 65"
              />
            </div>
            <div className="form-group">
              <label htmlFor="systolicBp">Systolic Blood Pressure (mmHg)</label>
              <input
                id="systolicBp"
                name="systolicBp"
                type="number"
                min="50"
                max="300"
                required
                value={form.systolicBp}
                onChange={handleChange}
                placeholder="e.g. 150"
              />
            </div>
            <div className="form-group">
              <label htmlFor="cholesterol">Cholesterol (mg/dL)</label>
              <input
                id="cholesterol"
                name="cholesterol"
                type="number"
                min="50"
                max="500"
                required
                value={form.cholesterol}
                onChange={handleChange}
                placeholder="e.g. 250"
              />
            </div>
            <div className="form-group">
              <label htmlFor="glucose">Glucose (mg/dL)</label>
              <input
                id="glucose"
                name="glucose"
                type="number"
                min="20"
                max="500"
                required
                value={form.glucose}
                onChange={handleChange}
                placeholder="e.g. 130"
              />
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
                required
                value={form.bmi}
                onChange={handleChange}
                placeholder="e.g. 32.5"
              />
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
              <div className="result-row">
                <span className="result-label">Risk Score</span>
                <span className="result-value">{result.riskScore}</span>
              </div>
              <div className="result-row">
                <span className="result-label">Risk Level</span>
                <span className={riskLevelClass}>{result.riskLevel}</span>
              </div>
              <div className="result-row explanation">
                <span className="result-label">Explanation</span>
                <p className="result-value">{result.explanation}</p>
              </div>
            </div>
          </section>
        )}
      </main>

      <footer className="footer">
        <p>CDSS MVP &mdash; For demonstration purposes only. Not for clinical use.</p>
      </footer>
    </div>
  );
}

export default App;
