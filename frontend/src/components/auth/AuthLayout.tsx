import type { ReactNode } from "react";

interface AuthLayoutProps {
    children: ReactNode;
    variant?: "login" | "signup";
}

function AuthLayout({ children, variant = "login" }: AuthLayoutProps) {
    return (
        <main className={`auth-page auth-page--${variant}`}>
            <section className="auth-image">
                {variant === "login" ? (
                    <img
                        src="/assets/login/sideimg.webp"
                        alt="Paisaje agrícola del Valle del Cauca"
                    />
                ) : (
                    <img
                        src="/assets/logo-verde.webp"
                        alt="AgroValle Connect"
                        className="auth-logo"
                    />
                )}
            </section>

            <section className="auth-content">{children}</section>
        </main>
    );
}

export default AuthLayout;
