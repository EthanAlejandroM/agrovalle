import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import AuthLayout from "../../components/auth/AuthLayout";
import FormField from "../../components/auth/FormField";
import PasswordInput from "../../components/auth/PasswordInput";
import { loginSchema, type LoginFormInput } from "../../schemas/authSchemas";
import { parseApiError } from "../../services/api";
import { authService } from "../../services/authService";
import { saveToken } from "../../utils/session";

interface Notice {
    type: "error" | "success";
    text: string;
}

function LoginPage() {
    const navigate = useNavigate();
    const location = useLocation();

    const [notice, setNotice] = useState<Notice | null>(
        (location.state as { registered?: boolean } | null)?.registered
            ? { type: "success", text: "Tu cuenta quedó creada. Ya puedes iniciar sesión." }
            : null
    );

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<LoginFormInput>({
        resolver: zodResolver(loginSchema),
        defaultValues: { correo: "", contrasena: "", recordarme: false },
    });

    const onSubmit = async (values: LoginFormInput) => {
        setNotice(null);
        try {
            const response = await authService.login({
                correo: values.correo,
                contrasena: values.contrasena,
            });
            saveToken(response.accessToken, Boolean(values.recordarme));
            navigate("/inicio", { replace: true });
        } catch (error) {
            const apiError = parseApiError(error);
            setNotice({
                type: "error",
                text:
                    apiError.codigo === "CREDENCIALES_INVALIDAS"
                        ? "El correo o la contraseña no coinciden. Revisa los datos e inténtalo de nuevo."
                        : apiError.mensaje,
            });
        }
    };

    return (
        <AuthLayout variant="login">
            <div className="login-container">
                <header className="login-header">
                    <h1>Bienvenido a AgroValle!</h1>
                    <p>Donde el Valle produce y AgroValle conecta.</p>
                </header>

                {notice && (
                    <p className={`notice notice--${notice.type}`} role={notice.type === "error" ? "alert" : "status"}>
                        {notice.text}
                    </p>
                )}

                <form className="login-form" onSubmit={handleSubmit(onSubmit)} noValidate>
                    <FormField id="correo" label="Email" error={errors.correo?.message}>
                        <input
                            id="correo"
                            type="email"
                            placeholder="ej: pepe@gmail.com"
                            autoComplete="email"
                            aria-invalid={errors.correo ? true : undefined}
                            aria-describedby={errors.correo ? "correo-error" : undefined}
                            {...register("correo")}
                        />
                    </FormField>

                    <FormField id="contrasena" label="Contraseña" error={errors.contrasena?.message}>
                        <PasswordInput
                            id="contrasena"
                            placeholder="Tu contraseña"
                            autoComplete="current-password"
                            registration={register("contrasena")}
                            hasError={Boolean(errors.contrasena)}
                        />
                    </FormField>

                    <div className="login-options">
                        <label className="remember-option">
                            <input type="checkbox" {...register("recordarme")} />
                            <span>Recuérdame</span>
                        </label>

                        <a
                            href="#"
                            className="forgot-password"
                            onClick={(event) => {
                                event.preventDefault();
                                setNotice({
                                    type: "success",
                                    text: "La recuperación de contraseña estará disponible pronto.",
                                });
                            }}
                        >
                            ¿Olvidaste la contraseña?
                        </a>
                    </div>

                    <button type="submit" className="login-button" disabled={isSubmitting}>
                        {isSubmitting ? "Entrando…" : "Inicia sesión"}
                    </button>

                    <div className="register-link">
                        <span>¿No tienes una cuenta?</span>
                        <Link to="/signup">Regístrate</Link>
                    </div>
                </form>
            </div>
        </AuthLayout>
    );
}

export default LoginPage;
