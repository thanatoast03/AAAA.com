import { React, useState, useEffect} from "react";
import messageGraphic from "../assets/graphics/messageGraphic.png";

const Register = () => {
    const [captchaToken, setCaptchaToken] = useState("");
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    
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

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!window.grecaptcha) {
            alert("ReCAPTCHA not loaded yet");
            return;
        }
    
        try {
            const token = await window.grecaptcha.execute(process.env.REACT_APP_SITE_KEY, { action: "submit" });
            
            const formData = new FormData();
            formData.append("username", username);
            formData.append("email", email);
            formData.append("password", password);
            formData.append("confirmPassword", confirmPassword);
            formData.append("g-recaptcha-response", token); // use token directly
    
            const response = await fetch("/api/register", {
                method: "POST",
                body: formData,
            });
            
            const data = await response.json();
            console.log("Success: ", data);
        } catch (error) {
            console.error("Error: ", error);
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
                        <button type="submit" className="w-2/3 text-white p-2 rounded font-casual bg-[#1AC472]">Register</button>
                    </form>
                    
                </div>
            </div>
           
        </div>
    );
};

export default Register;