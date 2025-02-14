import React from "react";
import { useNavigate } from "react-router-dom";

const Landing = () => {
    const navigate = useNavigate();
    const handleRegisterClick = () => {
        navigate("/register");
    };

    return (
        <div className="flex flex-col h-full text-white sm:pl-10 justify-center sm:justify-start">
            <div className="justify-center sm:justify-start text-center sm:text-start">
                <div className="sm:pt-20 ">
                    <h1 className="text-lg sm:text-2xl md:text-3xl lg:text-4xl">Hello! This is a chatroom for people to chat in!</h1>
                    <h1 className="text-xl sm:text-2xl md:text-3xl lg:text-4xl">Welcome to AAAA .com!</h1>
                    <br/>
                    <small>(Yes we know that's not the actual domain)</small>
                </div>
                <div className="pt-8 sm:pt-16">
                    <h1 className="text-2xl sm:text-3xl sm:pb-5">Sign up to start chatting here!</h1>
                    <button className="font-casual text-xl sm:text-2xl py-4 px-10 sm:py-6 sm:px-16 mt-3 sm:mt-0 w-fit rounded bg-[#1AC472]">
                        <h1 onClick={handleRegisterClick}>Sign Up!</h1>
                    </button>
                </div>
            </div>
        </div>
    )
}

export default Landing;