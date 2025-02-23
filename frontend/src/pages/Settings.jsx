import { React, useState, useEffect } from "react";
import './settings.css';
import online from '../assets/graphics/online.png';

const Settings = () => {

    return (
        <div className="settingsContainer">
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
                        <button className="settingsDelete">DELETE ACCOUNT</button>
                    </div>
                </div>
        </div>
    )
}

export default Settings;