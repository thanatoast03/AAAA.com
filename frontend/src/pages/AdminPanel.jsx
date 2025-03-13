import React, { useState, useEffect } from 'react';
import './AdminPanel.css';
import {useNavigate} from "react-router-dom";

const AdminPanel = () => {
  const [reportedMessages, setReportedMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  //! MIGHT NOT CORRECTLY VERIFY ADMIN STATUS. RELY ON BACKEND
  const verifyAdmin = async () => {
      try{
        const response = await fetch(process.env.REACT_APP_FETCH_PATH + "/verify/", {
            method: "GET",
            headers: {
              "Authorization" : `Bearer ${sessionStorage.getItem("token")}`
            }
        }); // returns id, username, role

        const responseJson = await response.json();

        if (!response.ok || responseJson.role !== "admin"){
            throw new Error("user is not an admin");
        }

      } catch (error){
          console.log(error);
          navigate("/login");
      }

  }
  const fetchReportedMessages = async () => {
      try {
          // Fetch reported messages
          const response = await fetch(process.env.REACT_APP_FETCH_PATH + '/messages/reported', {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': 'Bearer ' + sessionStorage.getItem('token'),
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
    verifyAdmin();
    fetchReportedMessages();
  }, []);

  if (loading) return <div>Loading...</div>;

  return (
      <div className="flex flex-col">
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
                  </tr>
                  </thead>
                  <tbody>
                  {reportedMessages.map((message) => (
                      <tr key={message.id}>
                        <td>{message.id}</td>
                        <td>{message.messageText}</td>
                      </tr>
                  ))}
                  </tbody>
                </table>
            )}
          </div>
          <div className="section">
            <h2>Top Reported Accounts</h2>
            {reportedMessages.length === 0 ? (
                <p>No Reported Accounts</p>
            ) : (
                <table>
                  <tbody>
                  {reportedMessages.map((message) => (
                      <tr key={message.id}>
                        <td>{message.creatorUsername}</td>
                      </tr>
                  ))}
                  </tbody>
                </table>
            )}
          </div>
          <div className="section">
            <h2>Sensitive Info Changes:</h2>
          </div>
        </div>
        <div className="delete-account">
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