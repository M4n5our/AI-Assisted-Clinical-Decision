import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import { getUsers, updateUserRole, getAllFeedback } from '../api';

function AdminPage() {
  const { isAdmin } = useAuth();
  const [users, setUsers] = useState([]);
  const [feedback, setFeedback] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isAdmin) return;
    async function load() {
      try {
        const [u, f] = await Promise.all([getUsers(), getAllFeedback()]);
        setUsers(u);
        setFeedback(f);
      } catch (err) {
        setError(err.message);
      }
    }
    load();
  }, [isAdmin]);

  const handleRoleChange = async (userId, newRole) => {
    try {
      await updateUserRole(userId, newRole);
      setUsers(users.map((u) => (u.id === userId ? { ...u, role: newRole } : u)));
    } catch (err) {
      setError(err.message);
    }
  };

  if (!isAdmin) return <div className="page"><p>Access denied. Admin only.</p></div>;

  return (
    <div className="page">
      <h2>Admin Panel</h2>
      {error && <div className="error-msg">{error}</div>}

      <section className="section">
        <h3>User Management</h3>
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Username</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td>{u.username}</td>
                  <td>{u.fullName}</td>
                  <td>{u.email}</td>
                  <td><span className={`role-badge role-${u.role.toLowerCase()}`}>{u.role}</span></td>
                  <td>
                    <select
                      value={u.role}
                      onChange={(e) => handleRoleChange(u.id, e.target.value)}
                    >
                      <option value="USER">USER</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="section">
        <h3>All Feedback ({feedback.length})</h3>
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Suggestion</th>
                <th>User</th>
                <th>Rating</th>
                <th>Comment</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {feedback.length === 0 ? (
                <tr><td colSpan="6" className="empty">No feedback yet</td></tr>
              ) : (
                feedback.map((f) => (
                  <tr key={f.id}>
                    <td>{f.id}</td>
                    <td>#{f.suggestionId}</td>
                    <td>{f.username}</td>
                    <td>{'*'.repeat(f.rating)}</td>
                    <td>{f.comment || '-'}</td>
                    <td>{f.createdAt ? new Date(f.createdAt).toLocaleString() : '-'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>
    </div>
  );
}

export default AdminPage;
