import { React, useState, useEffect } from "react";
import './settings.css';
import online from '../assets/graphics/online.png';
import DeleteAccountModal from "../modals/DeleteAccountModal";

const Settings = () => {
    const [deleteOpen,setDeleteOpen] = useState(false);
    const [newUsername,setNewUsername] = useState("");
    const [changeStatus,setChangeStatus] = useState("");


    const openDeleteModal = () => {
        setDeleteOpen(true);
    }

    const closeDeleteModal = () => {
        setDeleteOpen(false);
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
            setChangeStatus(data.message);
        } catch (error) {
            setChangeStatus(error.message);
        }
    }

    const handleUserChange = (e) => {
        setNewUsername(e.target.value); //updates newUsername with input
    };

    return (
        <div className="settingsContainer">
            {deleteOpen && <DeleteAccountModal closeDeleteModal={closeDeleteModal}/>}
                <div className="accountInfo">
                    <div className="accountSettings">
                        <h1>Account Settings</h1>
                        <img src={online}/>
                        <button className="settingsPfp">Change Profile Picture</button>
                        <button className="settingsLogout">Log Out</button>
                    </div>
                    <div className="accountPanel">
                        <div className="accountOption">
                            <h1>Username:</h1>
                            <input type="text" placeholder="username" onChange={handleUserChange}/>
                        </div>
                        <div className="accountOption">
                            <h1>Password:</h1>
                            <input type="text" placeholder="password"/>
                        </div>
                        <div className="accountOption">
                            <h1>Email:</h1>
                            <input type="text" placeholder="email"/>
                        </div>
                        <button className="settingsSave" onClick={() => changeUsername()}>Save Changes</button>
                        <p className="text-[#00FF00]">{changeStatus}</p>
                        <button className="settingsDelete" onClick={() => openDeleteModal()}>DELETE ACCOUNT</button>
                    </div>
                </div>
        </div>
    )
}

export default Settings;