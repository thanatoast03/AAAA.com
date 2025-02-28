import React from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";

const Navbar = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const handleRegisterClick = () => {
        navigate("/register");
    };

    const navigateLogin = () => {
        navigate("/login");
    }

    const navigateSettings = () => {
        navigate("/settings");
    }

    const navigateChatroom = () => {
        navigate("/chatroom")
    }

    const navigateLanding = () => {
        navigate("/")
    }

    //todo: check if authenticated
    //todo: check if admin

    return (
        <nav className="text-white flex justify-between bg-[#202020] text-3xl shadow-xl">
            <h1 className="py-3 px-5 sm:ml-5 hover:cursor-pointer" onClick={() => navigateLanding()}>AAAA</h1>
            <div className="flex flex-row text-2xl px-5 sm:mr-2">
                <h1 className="p-3 hover:cursor-pointer" onClick={() => handleRegisterClick()}>Register</h1>
                <h1 className="py-3">|</h1>
                <h1 className="p-3 hover:cursor-pointer" onClick={() => navigateLogin()}>Login</h1>
            </div>
        </nav>
    );
};

export default Navbar;