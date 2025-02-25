import React from 'react';
import './AdminPanel.css';

const AdminPanel = () => {
  return (
    <div className="admin-panel">
      <div className="section">
        <h2>Top Reported Messages:</h2>
      </div>

      <div className="section">
        <h2>Most Reported Users:</h2>
      </div>

      <div className="section">
        <h2>Sensitive Info Changes:</h2>
      </div>
    </div>
  );
};

export default AdminPanel;