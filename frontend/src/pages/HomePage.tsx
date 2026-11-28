import { useNavigate } from "react-router-dom";

import { clearToken, getSession } from "../utils/session";

// Pantalla provisional posterior al login (el "Inicio" del mockup se desarrolla después).
function HomePage() {
    const navigate = useNavigate();
    const session = getSession();

    const logout = () => {
        clearToken();
        navigate("/login", { replace: true });
    };

    return (
        <main className="home-placeholder">
            <img src="/assets/logo-verde.webp" alt="" width={120} />
            <h1>¡Bienvenido a AgroValle Connect!</h1>
            <p>
                Sesión iniciada como <strong>{session?.correo}</strong> ({session?.rol.toLowerCase()}).
            </p>
            <button type="button" className="login-button home-placeholder__button" onClick={logout}>
                Cerrar sesión
            </button>
        </main>
    );
}

export default HomePage;
