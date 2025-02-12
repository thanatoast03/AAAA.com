import { React, useState, useEffect} from "react";
import messageGraphic from "../assets/graphics/messageGraphic.png";

const Login = () => {
    const [captchaToken, setCaptchaToken] = useState("");

    useEffect (() => {
        const script = document.createElement("script");
        script.src = "https://www.google.com/recaptcha/api.js";
        script.async = true;
        script.defer = true;
        document.body.appendChild(script);
    }, [])

    const handleSubmit = (e) => {
        e.preventDefault();

        if(!captchaToken) {
            alert("Please complete the captcha");
            return;
        }

        const formData = newFormData(e.target);
        formData.append("g-recaptcha-response",captchaToken);

        fetch("backend-endpoint", {
            method: "POST",
            body: formData,
        })
        .then((response) => response.json())
        .then((data) => {
            console.log("Success: ", data);
        })
        .catch((error) => {
            console.error("Error: ", data)
        })
    }

    return (
        <div className="flex w-full h-screen">
            <div className="w-3/6 p-20 flex justify-center items-center">
                <img src={messageGraphic} alt="Message Graphic" />
            </div>
            <div className="w-3/6 p-4 flex justify-center items-center">
                <div className="p-14 rounded-xl shadow-2xl w-97 flex flex-col gap-4" style={{backgroundColor: "#2A2A2A"}}>
                    <h1 className="text-6xl font-bold text-white pb-10">Login</h1>
                    <form action="your-backend-endpoint" method="POST">
                        <input type="text" placeholder="Username" className="border border-gray-600 p-2 rounded w-full text-white placeholder-gray-400 mb-10" style={{backgroundColor: "#443F3F"}}/>
                        <input type="password" placeholder="Password" className="border border-gray-600 p-2 rounded w-full text-white placeholder-gray-400 mb-10" style={{backgroundColor: "#443F3F"}}/>
                        <div class="g-recaptcha" data-sitekey="YOUR_SITE_KEY"></div>
                    </form>
                    <button type="submit" className="w-1/2 text-white p-2 rounded w-full" style={{ backgroundColor: "#1Ac472"}}>Login!</button>
                </div>
            </div>
        </div>
    )
}

export default Login;