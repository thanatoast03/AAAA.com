import React from 'react';
import { HashRouter, Routes, Route } from 'react-router-dom';
import './index.css';  // import tailwind css file
import Landing from './pages/Landing.jsx'

function App() {
    return (
        <div className='h-screen flex flex-col'>
            <HashRouter>
                <Routes>
                    <Route path="/" element={<Landing />} />
                </Routes>
            </HashRouter>
        </div>
    )
}

export default App;