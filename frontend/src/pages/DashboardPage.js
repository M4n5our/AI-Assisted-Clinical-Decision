import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import { getPatients, getAllSuggestions, getAllFeedback } from '../api';

function DashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState({ patients: 0, suggestions: 0, feedback: 0 });
  const [recentSuggestions, setRecentSuggestions] = useState([]);

  useEffect(() => {
    async function loadDashboard() {
      try {
        const [patients, suggestions, feedback] = await Promise.all([
          getPatients(),
          getAllSuggestions(),
          getAllFeedback(),
        ]);
        setStats({
          patients: patients.length,
          suggestions: suggestions.length,
          feedback: feedback.length,
        });
        setRecentSuggestions(suggestions.slice(0, 5));
      } catch {
        // Stats will show 0 if backend isn't available
      }
    }
    loadDashboard();
  }, []);

  return (
    <div className="page">
      <h2>Welcome, {user?.username}</h2>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-number">{stats.patients}</div>
          <div className="stat-label">Patients</div>
          <Link to="/patients" className="stat-link">View All</Link>
        </div>
        <div className="stat-card">
          <div className="stat-number">{stats.suggestions}</div>
          <div className="stat-label">Suggestions</div>
          <Link to="/suggestions" className="stat-link">View All</Link>
        </div>
        <div className="stat-card">
          <div className="stat-number">{stats.feedback}</div>
          <div className="stat-label">Feedback</div>
        </div>
      </div>

      {recentSuggestions.length > 0 && (
        <section className="section">
          <h3>Recent Suggestions</h3>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Risk Level</th>
                  <th>Risk Score</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {recentSuggestions.map((s) => (
                  <tr key={s.id}>
                    <td>{s.id}</td>
                    <td><span className={`risk-badge risk-${s.riskLevel.toLowerCase()}`}>{s.riskLevel}</span></td>
                    <td>{(s.riskScore * 100).toFixed(0)}%</td>
                    <td>{s.createdAt ? new Date(s.createdAt).toLocaleString() : '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}
    </div>
  );
}

export default DashboardPage;
