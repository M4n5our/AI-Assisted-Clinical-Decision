import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import { getTemplates, createTemplate, deleteTemplate } from '../api';

function TemplatesPage() {
  const { isAdmin } = useAuth();
  const [templates, setTemplates] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: '', content: '', category: '' });
  const [error, setError] = useState('');

  const loadTemplates = async () => {
    try {
      const data = await getTemplates();
      setTemplates(data);
    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => { loadTemplates(); }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await createTemplate(form);
      setForm({ name: '', content: '', category: '' });
      setShowForm(false);
      loadTemplates();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this template?')) return;
    try {
      await deleteTemplate(id);
      loadTemplates();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h2>Templates</h2>
        {isAdmin && (
          <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>
            {showForm ? 'Cancel' : '+ New Template'}
          </button>
        )}
      </div>

      {error && <div className="error-msg">{error}</div>}

      {showForm && (
        <div className="form-card">
          <h3>Add Template</h3>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Name</label>
              <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Category</label>
              <input value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} required placeholder="e.g. cardiovascular, endocrine" />
            </div>
            <div className="form-group">
              <label>Content</label>
              <textarea value={form.content} onChange={(e) => setForm({ ...form, content: e.target.value })} required rows="4" />
            </div>
            <button type="submit" className="btn btn-primary">Save Template</button>
          </form>
        </div>
      )}

      <div className="cards-grid">
        {templates.length === 0 ? (
          <p className="empty">No templates found</p>
        ) : (
          templates.map((t) => (
            <div key={t.id} className="card">
              <div className="card-header">
                <strong>{t.name}</strong>
                <span className="category-badge">{t.category}</span>
              </div>
              <p className="card-notes">{t.content}</p>
              {isAdmin && (
                <button className="btn btn-sm btn-danger" onClick={() => handleDelete(t.id)}>Delete</button>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default TemplatesPage;
