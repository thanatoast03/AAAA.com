import React from "react";

const Login = () => {
    return (
        <div className="flex w-full h-screen">
            <div className="w-3/5 bg-gray-100 p-4">
                something
            </div>
            <div className="w-2/5 p-4 flex justify-center items-center">
                <div className="p-10 rounded-xl shadow-2xl w-80 flex flex-col gap-4" style={{ backgroundColor: "#2A2A2A" }}>
                    <h1 className="text-xl font-bold text-white">Login</h1>
                    <input 
                        type="text" 
                        placeholder="Username" 
                        className="border border-gray-600 p-2 rounded w-full text-white placeholder-gray-400"
                        style={{backgroundColor: "#443F3F"}}
                    />
                    <input 
                        type="password" 
                        placeholder="Password" 
                        className="border border-gray-600 p-2 rounded w-full text-white placeholder-gray-400"
                        style={{backgroundColor: "#443F3F"}}
                    />
                    <button className="w-1/2 text-white p-2 rounded w-full" style={{ backgroundColor: "#1Ac472"}}>Login!</button>
                </div>
            </div>
        </div>
    )
}

export default Login;