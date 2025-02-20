import React from 'react';
import { BrowserRouter, Routes, Route, Outlet } from 'react-router-dom';
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

function App() {
    return (
        <div className='h-screen max-h-screen flex flex-col bg-[#353535]'>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Layout />} >
                        <Route index element={<Landing /> } />
                        <Route path="register" element={<Register />} />
                        <Route path="login" element={<Login />} />
                        <Route path="chatroom" element={<Chatroom />}/>
                        <Route path="settings" element={<Settings />}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;