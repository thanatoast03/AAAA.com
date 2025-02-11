import React from "react";

const Landing = () => {
    return (
        <div className="flex flex-col h-full justify-center sm:justify-start text-white sm:pl-10 items-center sm:items-start">
            <div className="items-center">
                <div className="sm:pt-20 justify-self-center sm:justify-self-left">
                    <h1 className="text-lg sm:text-2xl md:text-3xl lg:text-4xl">Hello! This is a chatroom for people to chat in!</h1>
                    <h1 className="text-xl sm:text-2xl md:text-3xl lg:text-4xl">Welcome to AAAA .com!</h1>
                    <br/>
                    <small>(Yes we know that's not the actual domain)</small>
                </div>
                <div className="pt-16">
                    <h1 className="text-2xl sm:text-3xl pb-5">Sign up to start chatting here!</h1>
                    <button className="font-casual text-xl sm:text-2xl py-4 px-10 sm:py-6 sm:px-16 w-fit rounded bg-[#1AC472]">
                        <h1>Sign Up!</h1>
                    </button>
                </div>
            </div>
        </div>
    )
}

export default Landing;