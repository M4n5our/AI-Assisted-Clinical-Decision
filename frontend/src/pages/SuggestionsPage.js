import React, { useState, useEffect } from 'react';
import { getAllSuggestions } from '../api';

function SuggestionsPage() {
  const [suggestions, setSuggestions] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    async function load() {
      try {
        const data = await getAllSuggestions();
        setSuggestions(data);
      } catch (err) {
        setError(err.message);
      }
    }
    load();
  }, []);

  return (
    <div className="page">
      <h2>All Suggestions</h2>
      {error && <div className="error-msg">{error}</div>}

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Encounter</th>
              <th>Risk Level</th>
              <th>Risk Score</th>
              <th>Explanation</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {suggestions.length === 0 ? (
              <tr><td colSpan="6" className="empty">No suggestions found</td></tr>
            ) : (
              suggestions.map((s) => (
                <tr key={s.id}>
                  <td>{s.id}</td>
                  <td>#{s.encounterId}</td>
                  <td><span className={`risk-badge risk-${s.riskLevel.toLowerCase()}`}>{s.riskLevel}</span></td>
                  <td>{(s.riskScore * 100).toFixed(0)}%</td>
                  <td className="truncate">{s.explanation}</td>
                  <td>{s.createdAt ? new Date(s.createdAt).toLocaleString() : '-'}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default SuggestionsPage;
