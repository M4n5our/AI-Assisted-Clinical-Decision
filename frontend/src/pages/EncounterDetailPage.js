import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getEncounter, getSuggestionsByEncounter, generateSuggestion, createFeedback } from '../api';

function EncounterDetailPage() {
  const { id } = useParams();
  const [encounter, setEncounter] = useState(null);
  const [suggestions, setSuggestions] = useState([]);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState('');
  const [feedbackForm, setFeedbackForm] = useState({});

  const loadData = async () => {
    try {
      const [enc, sugs] = await Promise.all([getEncounter(id), getSuggestionsByEncounter(id)]);
      setEncounter(enc);
      setSuggestions(sugs);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => { loadData(); }, [id]);

  const handleGenerate = async () => {
    setError('');
    setGenerating(true);
    try {
      await generateSuggestion(id);
      loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setGenerating(false);
    }
  };

  const handleFeedback = async (suggestionId) => {
    const fb = feedbackForm[suggestionId];
    if (!fb || !fb.rating) return;
    try {
      await createFeedback({ suggestionId, rating: parseInt(fb.rating), comment: fb.comment || '' });
      setFeedbackForm({ ...feedbackForm, [suggestionId]: { rating: '', comment: '', submitted: true } });
    } catch (err) {
      setError(err.message);
    }
  };

  if (!encounter) return <div className="page"><p>Loading...</p></div>;

  return (
    <div className="page">
      <Link to={`/patients/${encounter.patientId}`} className="back-link">Back to Patient</Link>
      <h2>Encounter #{encounter.id}</h2>
      <p className="subtitle">Patient: {encounter.patientName} | Date: {encounter.createdAt ? new Date(encounter.createdAt).toLocaleString() : ''}</p>

      {encounter.notes && (
        <div className="card">
          <h4>Notes</h4>
          <p>{encounter.notes}</p>
        </div>
      )}

      {encounter.observations && encounter.observations.length > 0 && (
        <div className="card">
          <h4>Observations</h4>
          <div className="obs-list">
            {encounter.observations.map((obs) => (
              <span key={obs.id} className="obs-tag">{obs.type}: {obs.value} {obs.unit}</span>
            ))}
          </div>
        </div>
      )}

      {error && <div className="error-msg">{error}</div>}

      <div className="page-header">
        <h3>AI Suggestions</h3>
        <button className="btn btn-primary" onClick={handleGenerate} disabled={generating}>
          {generating ? 'Generating...' : 'Generate Risk Assessment'}
        </button>
      </div>

      {suggestions.length === 0 ? (
        <p className="empty">No suggestions yet. Click "Generate Risk Assessment" to analyze this encounter.</p>
      ) : (
        suggestions.map((s) => (
          <div key={s.id} className="result-section">
            <div className="result-card">
              <div className="risk-score-display">
                <div className="risk-score-number">{(s.riskScore * 100).toFixed(0)}%</div>
                <span className={`risk-badge risk-${s.riskLevel.toLowerCase()}`}>{s.riskLevel}</span>
              </div>
              <div className="risk-bar-container">
                <div className={`risk-bar risk-bar-${s.riskLevel.toLowerCase()}`} style={{ width: `${s.riskScore * 100}%` }} />
              </div>
              <div className="result-row explanation">
                <span className="result-label">Explanation</span>
                <p className="result-value">{s.explanation}</p>
              </div>

              {/* Feedback section */}
              <div className="feedback-section">
                <h4>Feedback</h4>
                {feedbackForm[s.id]?.submitted ? (
                  <p className="success-msg">Feedback submitted. Thank you!</p>
                ) : (
                  <div className="feedback-form">
                    <div className="form-row">
                      <div className="form-group">
                        <label>Rating (1-5)</label>
                        <select
                          value={feedbackForm[s.id]?.rating || ''}
                          onChange={(e) => setFeedbackForm({ ...feedbackForm, [s.id]: { ...feedbackForm[s.id], rating: e.target.value } })}
                        >
                          <option value="">Select</option>
                          <option value="1">1 - Poor</option>
                          <option value="2">2 - Fair</option>
                          <option value="3">3 - Good</option>
                          <option value="4">4 - Very Good</option>
                          <option value="5">5 - Excellent</option>
                        </select>
                      </div>
                      <div className="form-group">
                        <label>Comment</label>
                        <input
                          value={feedbackForm[s.id]?.comment || ''}
                          onChange={(e) => setFeedbackForm({ ...feedbackForm, [s.id]: { ...feedbackForm[s.id], comment: e.target.value } })}
                          placeholder="Optional comment"
                        />
                      </div>
                    </div>
                    <button className="btn btn-sm" onClick={() => handleFeedback(s.id)}>Submit Feedback</button>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );
}

export default EncounterDetailPage;
