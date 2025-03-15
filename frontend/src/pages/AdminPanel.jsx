import React, {useEffect, useState} from 'react';
import './AdminPanel.css';
import {useNavigate} from "react-router-dom";
import trashcan from "../assets/graphics/trashIcon.png";
import AdminDeleteModal from '../modals/adminDeleteModal';

const AdminPanel = () => {
  const [reportedMessages, setReportedMessages] = useState([]);
  const [reportedUsers, setReportedUsers] = useState([]);
  const [reportedMessageOccurrences, setReportedMessageOccurrences] = useState({});
  const [loading, setLoading] = useState(true);
  const [isAdminDeleteOpen,setIsAdminDeleteOpen] = useState(false);
  const [name2, setName2] = useState("");
  const [userNotFoundStatus,setUserNotFoundStatus] = useState("");
  const [userDeletedStatus,setUserDeletedStatus] = useState("");
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
              'Authorization': 'Bearer ' + sessionStorage.getItem('token'),
            },
          });

          if (!response.ok) {
            throw new Error('Failed to fetch reported messages');
          }

          const data = await response.json();
          setReportedMessages(data.reported_messages);
          setReportedUsers(Object.entries(data.reported_users));
          setReportedMessageOccurrences(data.reported_message_occurrences);
          setLoading(false);
      } catch (error) {
          console.error('Error fetching reported messages:', error);
          setLoading(false);
      }
  };

  const openAdminDeleteModal = () => {
    setIsAdminDeleteOpen(true);
  }

  const closeAdminDeleteModal = () => {
    setIsAdminDeleteOpen(false);
  }

  const setUserDeleted = (message) => {
    setUserDeletedStatus(message);
  }

  const setUserNotDeleted = (message) => {
    setUserDeletedStatus(message);
  }

  useEffect(() => {
    verifyAdmin();
    fetchReportedMessages();
  }, []);

  if (loading) return <div>Loading...</div>;

  const isLegitDelete = async() => {
    try{
        const response = await fetch(process.env.REACT_APP_ACCOUNTS_PATH + "/account-check",{
            method: "POST",
            headers: {
                "Authorization" : `Bearer ${sessionStorage.getItem("token")}`,
                "Content-Type" : "application/json"
            },
            body: JSON.stringify({
                "username" : name2
            })
        });
        const data = await response.json()
        if(response.ok){
            openAdminDeleteModal();
            setUserNotFoundStatus("");
        } else {
            setUserDeleted("");
            setUserNotFoundStatus(data.message);
        }
    } catch(e) {
        setUserNotFoundStatus("Error reaching backend: " + e);
    }
  }

  return (
      <div className="flex flex-col text-white font-casual w-full">
        {isAdminDeleteOpen && <AdminDeleteModal 
            closeDeleteModal={closeAdminDeleteModal} 
            name2={name2}
            setUserDeleted={setUserDeleted}
            setUserNotDeleted={setUserNotDeleted}
        />}
          <div className="admin-panel">
              <div className="section flex flex-col">
                  <h2 className="text-3xl sticky top-0 pb-2">Top Reported Messages:</h2>
                  <div className="overflow-y-auto flex-grow pr-2">
                      {reportedMessages.length === 0 ? (
                          <p>No reported messages</p>
                      ) : (
                          reportedMessages.map((message) => (
                            <div key={message.id} id={message.id} className="mb-4 p-3 border rounded relative">
                                <div className="flex-row text-xl">
                                    <p>{message.creatorUsername} - {reportedMessageOccurrences[message.messageId]}</p>
                                </div>
                                <p>{message.messageText}</p>
                                <img src={trashcan} onClick={(e) => deleteMessage(e)}className="absolute top-2 right-2 w-7 h-7 cursor-pointer" />
                            </div>
                          ))
                      )}
                  </div>
              </div>

              <div className="section flex flex-col">
                  <h2 className="text-3xl sticky top-0 pb-2">Top Reported Accounts</h2>
                  <div className="overflow-y-auto flex-grow pr-2">
                      {reportedUsers.length === 0 ? (
                          <p>No Reported Accounts</p>
                      ) : (
                          reportedUsers.map(([username, count]) => (
                              <div key={username} id={username} className="mb-4 p-3 border rounded">
                                  <p>{username} - {count}</p>
                              </div>
                          ))
                      )}
                  </div>
              </div>

              <div className="section flex flex-col">
                  <h2 className="text-3xl sticky top-0 pb-2">Sensitive Info Changes:</h2>
                  <div className="overflow-y-auto flex-grow pr-2"></div>
              </div>
          </div>
          <div className="flex flex-col w-[calc(33%-20px)] ml-[20px] p-[15px] bg-[#2A2A2A] rounded-lg drop-shadow-xl">
              <h2 className="text-2xl">Delete Account:</h2>
              <div className="flex flex-col">
                  <input
                      type="text"
                      placeholder="Type username here..."
                      className="text-black p-2 mb-2 rounded"
                      maxLength="32"
                      onChange={(e)=>(setName2(e.target.value))}
                  />
                  <h3 className='userNotFoundStatus'>{userNotFoundStatus}</h3>
                  <h3 className='userDeletedStatus'>{userDeletedStatus}</h3>
                  <button className="bg-red-600 p-2 rounded hover:bg-red-700" onClick={() => isLegitDelete()}>Delete Account</button>
              </div>
          </div>
      </div>
  );
};

export default AdminPanel;