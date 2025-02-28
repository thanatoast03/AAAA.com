import { React, useState, useEffect } from "react";
import './settings.css';
import online from '../assets/graphics/online.png';
import DeleteAccountModal from "../modals/DeleteAccountModal";

const Settings = () => {
    const [deleteOpen,setDeleteOpen] = useState(false);

    const openDeleteModal = () => {
        setDeleteOpen(true);
    }

    const closeDeleteModal = () => {
        setDeleteOpen(false);
    }

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
                            <input type="text" placeholder="username"/>
                        </div>
                        <div className="accountOption">
                            <h1>Password:</h1>
                            <input type="text" placeholder="password"/>
                        </div>
                        <div className="accountOption">
                            <h1>Email:</h1>
                            <input type="text" placeholder="email"/>
                        </div>
                        <button className="settingsSave">Save Changes</button>
                        <button className="settingsDelete" onClick={() => openDeleteModal()}>DELETE ACCOUNT</button>
                    </div>
                </div>
        </div>
    )
}

export default Settings;