import { React, useState } from "react";
import './settings.css';
import online from '../assets/graphics/online.png';
import DeleteAccountModal from "../modals/DeleteAccountModal";
import { useNavigate } from "react-router-dom";

const Settings = () => {
    const [deleteOpen,setDeleteOpen] = useState(false);
    const [newUsername,setNewUsername] = useState("");
    const [newEmail,setNewEmail] = useState("");
    const [newPassword,setNewPassword] = useState("");
    const [changeUsernameStatus,setChangeUsernameStatus] = useState("");
    const [changeEmailStatus, setChangeEmailStatus] = useState("");
    const [changePasswordStatus, setChangePasswordStatus] = useState("");
    const navigate = useNavigate();


    const openDeleteModal = () => {
        setDeleteOpen(true);
    }

    const closeDeleteModal = () => {
        setDeleteOpen(false);
    }

    const changeSensitiveInfo = () => {
        if(newUsername !== ""){
            changeUsername();
        }
        if (newPassword !== ""){
            changePassword();
        }
        if (newEmail !== ""){
            changeEmail();
        }
    }

    const changeUsername = async() => {
        try{
            const response = await fetch(process.env.REACT_APP_ACCOUNTS_PATH+"/changeUsername", {
                method: "POST",
                headers: {
                    "Content-Type" : "application/json",
                    "Authorization" : `Bearer ${sessionStorage.getItem("token")}`,
                },
                body: JSON.stringify({
                    "newUsername" : newUsername,
                }),
            });
            const data = await response.json();
            if(data.success === "false") {
                setChangeUsernameStatus(data.message);
            } else {
                setNewUsername("");
                sessionStorage.setItem("token",data.token);
                setChangeUsernameStatus(data.message);
            }
        } catch (error) {
            setChangeUsernameStatus(error.message);
        }
    }

    const changeEmail = async() => {
        try{
            const response = await fetch(process.env.REACT_APP_ACCOUNTS_PATH+"/changeEmail", {
                method: "POST",
                headers: {
                    "Content-Type" : "application/json",
                    "Authorization" : `Bearer ${sessionStorage.getItem("token")}`,
                },
                body: JSON.stringify({
                    "newEmail" : newEmail,
                }),
            });
            const data = await response.json();
            if(data.success === "false"){
                setChangeUsernameStatus(data.message);
            } else {
                setNewEmail("");
                sessionStorage.setItem("token",data.token);
                setChangeEmailStatus(data.message);
            }
        } catch (error) {
            setChangeEmailStatus(error.message);
        }
    }

    const changePassword = async() => {
        try{
            const response = await fetch(process.env.REACT_APP_ACCOUNTS_PATH+"/changePassword", {
                method: "POST",
                headers: {
                    "Content-Type" : "application/json",
                    "Authorization" : `Bearer ${sessionStorage.getItem("token")}`,
                },
                body: JSON.stringify({
                    "newPassword" : newPassword,
                }),
            });
            const data = await response.json();
            setChangePasswordStatus(data.message);
            setNewPassword("");
        } catch (error) {
            setChangePasswordStatus(error.message);
        }
    }

    const handleUserChange = (e) => {
        setNewUsername(e.target.value); //updates newUsername with input
    };

    const handleEmailChange = (e) => {
        setNewEmail(e.target.value); //updates newEmail with input
    };

    const handlePasswordChange = (e) => {
        setNewPassword(e.target.value); //updates newPassword with input
    }

    const handleLogout = async() => {
        try{
            const response = await fetch(process.env.REACT_APP_ACCOUNTS_PATH + "/logout", {
                method: "POST",
                headers: {
                    "Content-Type" : "application/json",
                    "Authorization" : `Bearer ${sessionStorage.getItem("token")}`,
                },
            });
            const data = await response.json()
            if(response.ok){
                setChangePasswordStatus("");
                sessionStorage.removeItem("token");
                navigate("/login");
            }
        } catch (error) {
            setChangePasswordStatus("Failed to log out");
        }
    } 

    return (
        <div className="settingsContainer">
            {deleteOpen && <DeleteAccountModal closeDeleteModal={closeDeleteModal}/>}
                <div className="accountInfo">
                    <div className="accountSettings">
                        <h1>Account Settings</h1>
                        <img src={online}/>
                        <button className="settingsPfp">Change Profile Picture</button>
                        <button className="settingsLogout" onClick={() => handleLogout()}>Log Out</button>
                    </div>
                    <div className="accountPanel">
                        <div className="accountOption">
                            <h1>Username:</h1>
                            <input type="text" placeholder="username" onChange={handleUserChange}/>
                        </div>
                        <p className="text-[#00FF00]">{changeUsernameStatus}</p>
                        <div className="accountOption">
                            <h1>Password:</h1>
                            <input type="text" placeholder="password" onChange={handlePasswordChange}/>
                        </div>
                        <p className="text-[#00FF00]">{changePasswordStatus}</p>
                        <div className="accountOption">
                            <h1>Email:</h1>
                            <input type="text" placeholder="email" onChange={handleEmailChange}/>
                        </div>
                        <p className="text-[#00FF00]">{changeEmailStatus}</p>
                        <button className="settingsSave" onClick={() => changeSensitiveInfo()}>Save Changes</button>
                        <button className="settingsDelete" onClick={() => openDeleteModal()}>DELETE ACCOUNT</button>
                    </div>
                </div>
        </div>
    )
}

export default Settings;