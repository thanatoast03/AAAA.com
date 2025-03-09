import React, { useState, useEffect } from 'react';
import './AdminPanel.css';

const AdminPanel = () => {
  const [reportedMessages, setReportedMessages] = useState([]);
  const [loading, setLoading] = useState(true);

  // Fetch top reported messages from the backend
  const fetchReportedMessages = async () => {
    try {
      // Fetch reported messages
      const response = await fetch(process.env.REACT_APP_FETCH_PATH + '/messages/reported', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ' + sessionStorage.getItem('token'), // Use session storage for authentication
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch reported messages');
      }

      const data = await response.json();
      setReportedMessages(data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching reported messages:', error);
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReportedMessages();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="admin-panel">
      <div className="section">
        <h2>Top Reported Messages:</h2>
        {reportedMessages.length === 0 ? (
          <p>No reported messages</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Message ID</th>
                <th>Message Content</th>
                <th>Reporter</th>
                <th>Reported Time</th>
              </tr>
            </thead>
            <tbody>
              {reportedMessages.map((message) => (
                <tr key={message.id}>
                  <td>{message.id}</td>
                  <td>{message.text}</td>
                  <td>{message.reporter.username}</td>
                  <td>{message.reportedAt}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
      <div className="section">
        <h2>Top Reported Accounts</h2>
      </div>
      <div className="section">
        <h2>Sensitive Info Changes:</h2>
      </div>
      <div className="delete-account" >
        <div className="delete-input-container">
          <input
            type="text"
            placeholder="Type username here..."
          />
          <button className="delete-button">Delete Account</button>
    </div>
</div>
</div>
  );
};

export default AdminPanel;