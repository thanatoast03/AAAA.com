import { React, useState, useEffect } from "react";
import messageGraphic from "../assets/graphics/messageGraphic.png";
import { useNavigate } from "react-router-dom";

const Register = () => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [registerStatus, setRegisterStatus] = useState("");
    const navigate = useNavigate();
    
    useEffect(() => {
        const existingScript = document.querySelector(`script[src="https://www.google.com/recaptcha/api.js?render=${process.env.REACT_APP_SITE_KEY}"]`); // check if script already in html
        
        if (!existingScript) { // if no script, add
            const script = document.createElement("script");
            script.src = `https://www.google.com/recaptcha/api.js?render=${process.env.REACT_APP_SITE_KEY}`;
            script.async = true;
            script.defer = true;
            document.body.appendChild(script);
        }
    
        return () => { // if changing page, remove script
            const script = document.querySelector(`script[src="https://www.google.com/recaptcha/api.js?render=${process.env.REACT_APP_SITE_KEY}"]`);
            if (script) {
                script.remove();
            }

            // remove all ReCAPTCHA elements added
            document.querySelectorAll(".grecaptcha-badge, [src*='recaptcha/api.js']").forEach((el) => el.remove());
        }
    }, []);

    const navLog = () => {
        navigate("/login");
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!window.grecaptcha) {
            alert("ReCAPTCHA not loaded yet");
            return;
        }
    
        try {
            const token = await window.grecaptcha.execute(process.env.REACT_APP_SITE_KEY, { action: "submit" });
    
            const response = await fetch(process.env.REACT_APP_ACCOUNTS_PATH + "/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    "username": username,
                    "email": email, 
                    "password": password,
                    "confirmPassword": confirmPassword, 
                    "recaptchaToken": token
                }),
            })
            if (response.success){
                navLog();
            } else {
                setRegisterStatus(response.message);
            }
            
        } catch (error) {
            setRegisterStatus(error);
        }
    };

    return (
        <div className="flex h-full text-white sm:px-10 justify-center sm:justify-start">
            <div className="w-3/6 hidden sm:flex justify-center items-center">
                <img src={messageGraphic} alt="Message Graphic" className="aspect-auto max-w-[400px]"/>
            </div>
            <div className="sm:w-3/6 p-4 flex justify-center sm:justify-end items-center">
                <div className="p-14 rounded-xl shadow-2xl w-97 flex flex-col gap-4 bg-[#2A2A2A]">
                    <h1 className="text-4xl font-bold text-white pb-10">Create an Account</h1>
                    <form className="flex flex-col" onSubmit={handleSubmit} >
                        <input type="text" placeholder="Username" className="border border-gray-600 p-2 rounded text-white placeholder-gray-400 mb-5 bg-[#443F3F]"onChange={(e) => setUsername(e.target.value)} maxLength="32"/>
                        <input type="text" placeholder="Email" className="border border-gray-600 p-2 rounded text-white placeholder-gray-400 mb-5 bg-[#443F3F]"onChange={(e) => setEmail(e.target.value)} maxLength="320"/>
                        <input type="password" placeholder="Password" className="border border-gray-600 p-2 rounded text-white placeholder-gray-400 mb-5 bg-[#443F3F]"onChange={(e) => setPassword(e.target.value)} maxLength="128"/>
                        <input type="password" placeholder="Confirm Password" className="border border-gray-600 p-2 rounded text-white placeholder-gray-400 mb-10 bg-[#443F3F]"onChange={(e) => setConfirmPassword(e.target.value)} maxLength="128"/>
                        <p className="text-[#FF0000] mb-2 max-w-screen-sm">{registerStatus}</p>
                        <button type="submit" className="w-2/3 text-white p-2 rounded font-casual bg-[#1AC472]">Register</button>
                    </form>
                    
                </div>
            </div>
           
        </div>
    );
};

export default Register;