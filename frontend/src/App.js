import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import PatientsPage from './pages/PatientsPage';
import PatientDetailPage from './pages/PatientDetailPage';
import EncounterDetailPage from './pages/EncounterDetailPage';
import SuggestionsPage from './pages/SuggestionsPage';
import TemplatesPage from './pages/TemplatesPage';
import AdminPage from './pages/AdminPage';

function PrivateRoute({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" />;
}

function App() {
  return (
    <Router>
      <div className="app">
        <Navbar />
        <main className="main-content">
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/" element={<PrivateRoute><DashboardPage /></PrivateRoute>} />
            <Route path="/patients" element={<PrivateRoute><PatientsPage /></PrivateRoute>} />
            <Route path="/patients/:id" element={<PrivateRoute><PatientDetailPage /></PrivateRoute>} />
            <Route path="/encounters/:id" element={<PrivateRoute><EncounterDetailPage /></PrivateRoute>} />
            <Route path="/suggestions" element={<PrivateRoute><SuggestionsPage /></PrivateRoute>} />
            <Route path="/templates" element={<PrivateRoute><TemplatesPage /></PrivateRoute>} />
            <Route path="/admin" element={<PrivateRoute><AdminPage /></PrivateRoute>} />
            <Route path="*" element={<Navigate to="/" />} />
          </Routes>
        </main>
        <footer className="footer">
          <p>CDSS &mdash; For demonstration purposes only. Not for clinical use.</p>
        </footer>
      </div>
    </Router>
  );
}

export default App;
