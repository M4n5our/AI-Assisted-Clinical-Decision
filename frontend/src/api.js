const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

function getToken() {
  return localStorage.getItem('token');
}

function authHeaders() {
  const token = getToken();
  return token ? { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } : { 'Content-Type': 'application/json' };
}

async function request(path, options = {}) {
  const res = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: { ...authHeaders(), ...options.headers },
  });
  if (res.status === 401) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
    throw new Error('Session expired');
  }
  if (!res.ok) {
    const text = await res.text();
    let msg;
    try {
      const json = JSON.parse(text);
      msg = json.message || json.error || text;
    } catch {
      msg = text;
    }
    throw new Error(msg || `Error ${res.status}`);
  }
  if (res.status === 204) return null;
  return res.json();
}

// Auth
export const login = (data) => request('/api/auth/login', { method: 'POST', body: JSON.stringify(data) });
export const register = (data) => request('/api/auth/register', { method: 'POST', body: JSON.stringify(data) });

// Patients
export const getPatients = () => request('/api/patients');
export const getPatient = (id) => request(`/api/patients/${id}`);
export const createPatient = (data) => request('/api/patients', { method: 'POST', body: JSON.stringify(data) });
export const updatePatient = (id, data) => request(`/api/patients/${id}`, { method: 'PUT', body: JSON.stringify(data) });
export const deletePatient = (id) => request(`/api/patients/${id}`, { method: 'DELETE' });
export const searchPatients = (lastName) => request(`/api/patients/search?lastName=${encodeURIComponent(lastName)}`);

// Encounters
export const getEncountersByPatient = (patientId) => request(`/api/encounters/patient/${patientId}`);
export const getEncounter = (id) => request(`/api/encounters/${id}`);
export const createEncounter = (data) => request('/api/encounters', { method: 'POST', body: JSON.stringify(data) });

// Suggestions
export const generateSuggestion = (encounterId) => request(`/api/suggestions/generate/${encounterId}`, { method: 'POST' });
export const getSuggestionsByEncounter = (encounterId) => request(`/api/suggestions/encounter/${encounterId}`);
export const getAllSuggestions = () => request('/api/suggestions');

// Feedback
export const createFeedback = (data) => request('/api/feedback', { method: 'POST', body: JSON.stringify(data) });
export const getFeedbackBySuggestion = (suggestionId) => request(`/api/feedback/suggestion/${suggestionId}`);
export const getAllFeedback = () => request('/api/feedback');

// Templates
export const getTemplates = () => request('/api/templates');
export const createTemplate = (data) => request('/api/templates', { method: 'POST', body: JSON.stringify(data) });
export const updateTemplate = (id, data) => request(`/api/templates/${id}`, { method: 'PUT', body: JSON.stringify(data) });
export const deleteTemplate = (id) => request(`/api/templates/${id}`, { method: 'DELETE' });

// Admin
export const getUsers = () => request('/api/admin/users');
export const updateUserRole = (id, role) => request(`/api/admin/users/${id}/role`, { method: 'PUT', body: JSON.stringify({ role }) });

// Health
export const healthCheck = () => request('/api/health');
