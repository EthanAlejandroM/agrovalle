import type { ReactNode } from "react";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import HomePage from "./pages/HomePage";
import LoginPage from "./pages/auth/LoginPage";
import SignupPage from "./pages/auth/SignupPage";
import { getSession } from "./utils/session";

// Solo deja pasar con un token vigente.
const Private = ({ children }: { children: ReactNode }) =>
    getSession() ? children : <Navigate to="/login" replace />;

// Si ya hay sesión, login y registro no tienen sentido.
const Public = ({ children }: { children: ReactNode }) =>
    getSession() ? <Navigate to="/inicio" replace /> : children;

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route path="/login" element={<Public><LoginPage /></Public>} />
                <Route path="/signup" element={<Public><SignupPage /></Public>} />
                <Route path="/inicio" element={<Private><HomePage /></Private>} />
                <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
