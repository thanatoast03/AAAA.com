import { React, useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import './index.css';  // import tailwind css file
import Landing from './pages/Landing.jsx';
import Login from './pages/Login.jsx';
import Navbar from './Navbar.jsx';
import Register from './pages/Register.jsx';
import Chatroom from './pages/Chatroom.jsx';
import Settings from './pages/Settings.jsx';

function Layout() {
    return (
        <>
            <Navbar />
            <Outlet />    
        </>
    );
}

function ProtectedRoute() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    
    useEffect(() => {
        const verifyToken = async() => {
            try{
                const response = await fetch("/verify", {
                    method: "GET",
                    headers: {
                        "Authorization" : `Bearer ${sessionStorage.getItem("token")}`,
                        "Content-Type" : "application/json"
                    }
                });
                if (response.ok) {
                    const data = await response.json();
                    setIsAuthenticated(true);
                } else {
                    setIsAuthenticated(false);
                }
            } catch (error){
                setIsAuthenticated(false);
            } finally {
                setIsLoading(false);
            }
        }
        verifyToken();
    },[]);

    if (isLoading) {
        return <div className="flex justify-center items-center h-full">Loading...</div>
    }

    return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
}

function App() {
    return (
        <div className='h-screen max-h-screen flex flex-col bg-[#353535]'>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Layout />} >
                        <Route index element={<Landing /> } />
                        <Route path="register" element={<Register />} />
                        <Route path="login" element={<Login />} />
                        <Route element={<ProtectedRoute />}>
                            <Route path="chatroom" element={<Chatroom />}/>
                            <Route path="settings" element={<Settings />}/>
                        </Route>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;