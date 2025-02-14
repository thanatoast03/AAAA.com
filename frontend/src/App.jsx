import React from 'react';
import { HashRouter, Routes, Route } from 'react-router-dom';
import './index.css';  // import tailwind css file
import Landing from './pages/Landing.jsx'
import Navbar from './Navbar.jsx';

function App() {
    return (
        <div className='h-screen max-h-screen flex flex-col bg-[#353535]'>
            <HashRouter>
                <Navbar />
                <Routes>
                    <Route path="/" element={<Landing />} />
                </Routes>
            </HashRouter>
        </div>
    )
}

export default App;