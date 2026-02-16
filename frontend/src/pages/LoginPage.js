import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../AuthContext';
import { login, register } from '../api';

function LoginPage() {
  const [isRegister, setIsRegister] = useState(false);
  const [form, setForm] = useState({ username: '', password: '', fullName: '', email: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { loginUser } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      let data;
      if (isRegister) {
        data = await register(form);
      } else {
        data = await login({ username: form.username, password: form.password });
      }
      loginUser(data);
      navigate('/');
    } catch (err) {
      setError(err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-header">
          <div className="header-icon">&#9877;</div>
          <h1>Clinical Decision Support System</h1>
          <p>{isRegister ? 'Create an Account' : 'Sign In'}</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Username</label>
            <input name="username" value={form.username} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input name="password" type="password" value={form.password} onChange={handleChange} required />
          </div>
          {isRegister && (
            <>
              <div className="form-group">
                <label>Full Name</label>
                <input name="fullName" value={form.fullName} onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input name="email" type="email" value={form.email} onChange={handleChange} required />
              </div>
            </>
          )}
          {error && <div className="error-msg">{error}</div>}
          <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
            {loading ? 'Please wait...' : isRegister ? 'Register' : 'Login'}
          </button>
        </form>

        <p className="toggle-auth">
          {isRegister ? 'Already have an account?' : "Don't have an account?"}{' '}
          <button type="button" className="link-btn" onClick={() => { setIsRegister(!isRegister); setError(''); }}>
            {isRegister ? 'Sign In' : 'Register'}
          </button>
        </p>

        <div className="demo-credentials">
          <p><strong>Demo accounts:</strong></p>
          <p>Admin: admin / admin123</p>
          <p>Doctor: doctor / doctor123</p>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
