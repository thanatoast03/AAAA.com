import React, { useState, useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";


const Navbar = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const [user, setUser] = useState(null); 
    const [isAdmin, setIsAdmin] = useState(false); 

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

    const navigateAdmin = () => {
        if (isAdmin) {
            navigate("/admin panel");
        } else {
            navigate("/login"); 
        }
    }

    const navigateLanding = () => {
        navigate("/")
    }

    useEffect(() => {
        const fetchUser = async () => {
            try {

                const token = sessionStorage.getItem("token");
                if (!token) {
                    throw new Error("No token found");
                }

                const response = await fetch(process.env.REACT_APP_FETCH_PATH + '/verify/', {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`
                    }
                });

                if (!response.ok) {
                    throw new Error("User not authenticated");
                }

                const data = await response.json();
                setUser(data);
                console.log(data);
                setIsAdmin(data.role === "admin");
            } catch (error) {
                console.error("Error fetching user:", error);
                setUser(null);
                setIsAdmin(false);
            }
        };

        fetchUser();
    }, []);

    return (
        <nav className="text-white flex justify-between bg-[#202020] text-3xl shadow-xl">
            <h1 className="py-3 px-5 sm:ml-5 hover:cursor-pointer" onClick={navigateLanding}>AAAA</h1>
            <div className="flex flex-row text-2xl px-5 sm:mr-2">
                {user ? (
                    <>
                        {location.pathname === '/chatroom' && ( 
                            <>
                                {isAdmin && (
                                    <>
                                        <h1 className="p-3 hover:cursor-pointer" onClick={navigateAdmin}>Admin Panel</h1>
                                        <h1 className="py-3">|</h1>
                                    </>
                                )}
                                <h1 className="p-3 hover:cursor-pointer" onClick={navigateSettings}>Settings</h1>
                            </>
                        )}
                        {location.pathname === '/settings' && ( 
                            <>
                                {isAdmin && (
                                    <>
                                        <h1 className="p-3 hover:cursor-pointer" onClick={navigateAdmin}>Admin Panel</h1>
                                        <h1 className="py-3">|</h1>
                                    </>
                                )}
                                <h1 className="p-3 hover:cursor-pointer" onClick={navigateChatroom}>Chatroom</h1>
                            </>
                        )}
                        {isAdmin && (
                            <>
                                {location.pathname === '/admin panel' && (
                                    <>
                                        <h1 className="p-3 hover:cursor-pointer" onClick={navigateChatroom}>Chatroom</h1>
                                        <h1 className="py-3">|</h1>
                                        <h1 className="p-3 hover:cursor-pointer" onClick={navigateSettings}>Settings</h1>
                                    </>
                                )}
                            </>
                        )}
                    </>
                ) : (
                    <>  
                        {location.pathname === '/' && (
                            <>
                                <h1 className="p-3 hover:cursor-pointer" onClick={handleRegisterClick}>Register</h1>
                                <h1 className="py-3">|</h1>
                                <h1 className="p-3 hover:cursor-pointer" onClick={navigateLogin}>Login</h1>
                            </>
                        )}
                        {location.pathname === '/register' && (
                            <h1 className="p-3 hover:cursor-pointer" onClick={navigateLogin}>Login</h1>
                        )}
                        {location.pathname === '/login' && (
                            <h1 className="p-3 hover:cursor-pointer" onClick={handleRegisterClick}>Register</h1>
                        )}
                    </>
                )}
            </div>
        </nav>
    );
};

export default Navbar;