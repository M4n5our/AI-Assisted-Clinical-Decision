import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getPatient, getEncountersByPatient, createEncounter } from '../api';

function PatientDetailPage() {
  const { id } = useParams();
  const [patient, setPatient] = useState(null);
  const [encounters, setEncounters] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [notes, setNotes] = useState('');
  const [observations, setObservations] = useState([
    { type: 'age', value: '', unit: 'years' },
    { type: 'systolic_bp', value: '', unit: 'mmHg' },
    { type: 'cholesterol', value: '', unit: 'mg/dL' },
    { type: 'glucose', value: '', unit: 'mg/dL' },
    { type: 'bmi', value: '', unit: 'kg/m2' },
  ]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const loadData = async () => {
    try {
      const [p, enc] = await Promise.all([getPatient(id), getEncountersByPatient(id)]);
      setPatient(p);
      setEncounters(enc);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => { loadData(); }, [id]);

  const handleObsChange = (index, value) => {
    const updated = [...observations];
    updated[index].value = value;
    setObservations(updated);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const obs = observations
        .filter((o) => o.value !== '')
        .map((o) => ({ type: o.type, value: parseFloat(o.value), unit: o.unit }));

      await createEncounter({ patientId: parseInt(id), notes, observations: obs });
      setNotes('');
      setObservations([
        { type: 'age', value: '', unit: 'years' },
        { type: 'systolic_bp', value: '', unit: 'mmHg' },
        { type: 'cholesterol', value: '', unit: 'mg/dL' },
        { type: 'glucose', value: '', unit: 'mg/dL' },
        { type: 'bmi', value: '', unit: 'kg/m2' },
      ]);
      setShowForm(false);
      loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (!patient) return <div className="page"><p>Loading...</p></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <Link to="/patients" className="back-link">Back to Patients</Link>
          <h2>{patient.firstName} {patient.lastName}</h2>
          <p className="subtitle">DOB: {patient.dateOfBirth} | Gender: {patient.gender} | Phone: {patient.phone || '-'}</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>
          {showForm ? 'Cancel' : '+ New Encounter'}
        </button>
      </div>

      {error && <div className="error-msg">{error}</div>}

      {showForm && (
        <div className="form-card">
          <h3>New Encounter</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Notes</label>
              <textarea value={notes} onChange={(e) => setNotes(e.target.value)} rows="3" placeholder="Clinical notes..." />
            </div>
            <h4>Observations</h4>
            {observations.map((obs, i) => (
              <div key={obs.type} className="form-row">
                <div className="form-group">
                  <label>{obs.type.replace('_', ' ').toUpperCase()} ({obs.unit})</label>
                  <input
                    type="number"
                    step="any"
                    value={obs.value}
                    onChange={(e) => handleObsChange(i, e.target.value)}
                    placeholder={`Enter ${obs.type.replace('_', ' ')}`}
                  />
                </div>
              </div>
            ))}
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Creating...' : 'Create Encounter'}
            </button>
          </form>
        </div>
      )}

      <section className="section">
        <h3>Encounters ({encounters.length})</h3>
        {encounters.length === 0 ? (
          <p className="empty">No encounters yet</p>
        ) : (
          encounters.map((enc) => (
            <div key={enc.id} className="card">
              <div className="card-header">
                <Link to={`/encounters/${enc.id}`}><strong>Encounter #{enc.id}</strong></Link>
                <span className="date">{enc.createdAt ? new Date(enc.createdAt).toLocaleString() : ''}</span>
              </div>
              {enc.notes && <p className="card-notes">{enc.notes}</p>}
              {enc.observations && enc.observations.length > 0 && (
                <div className="obs-list">
                  {enc.observations.map((obs) => (
                    <span key={obs.id} className="obs-tag">{obs.type}: {obs.value} {obs.unit}</span>
                  ))}
                </div>
              )}
              <Link to={`/encounters/${enc.id}`} className="btn btn-sm">View Details</Link>
            </div>
          ))
        )}
      </section>
    </div>
  );
}

export default PatientDetailPage;
