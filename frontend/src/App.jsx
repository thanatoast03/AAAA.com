import {React, useEffect, useState} from 'react';
import {BrowserRouter, Navigate, Outlet, Route, Routes} from 'react-router-dom';
import './index.css'; // import tailwind css file
import Landing from './pages/Landing.jsx';
import Login from './pages/Login.jsx';
import Navbar from './Navbar.jsx';
import Register from './pages/Register.jsx';
import Chatroom from './pages/Chatroom.jsx';
import Settings from './pages/Settings.jsx';
import AdminPanel from './pages/AdminPanel.jsx';

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
                const response = await fetch(process.env.REACT_APP_FETCH_PATH + "/verify/", {
                    method: "GET",
                    headers: {
                        "Authorization" : `Bearer ${sessionStorage.getItem("token")}`
                    }
                });
                if (response.ok) {
                    setIsAuthenticated(true);
                } else {
                    throw new Error("not authenticated");
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
                        <Route element={<ProtectedRoute />} >
                            <Route path="chatroom" element={<Chatroom />}/>
                            <Route path="settings" element={<Settings />}/>
                            <Route path="admin-panel" element={<AdminPanel />}/>
                        </Route>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;