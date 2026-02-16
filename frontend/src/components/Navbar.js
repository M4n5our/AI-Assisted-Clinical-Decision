import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthContext';

function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (!user) return null;

  return (
    <nav className="navbar">
      <div className="nav-brand">
        <Link to="/">CDSS</Link>
      </div>
      <div className="nav-links">
        <Link to="/">Dashboard</Link>
        <Link to="/patients">Patients</Link>
        <Link to="/suggestions">Suggestions</Link>
        <Link to="/templates">Templates</Link>
        {isAdmin && <Link to="/admin">Admin</Link>}
      </div>
      <div className="nav-user">
        <span>{user.username} ({user.role})</span>
        <button onClick={handleLogout} className="btn btn-sm">Logout</button>
      </div>
    </nav>
  );
}

export default Navbar;
