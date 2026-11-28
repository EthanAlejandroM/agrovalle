import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

import AuthLayout from "../../components/auth/AuthLayout";
import FormField from "../../components/auth/FormField";
import PasswordInput from "../../components/auth/PasswordInput";
import {
    registroSchema,
    type RegistroFormInput,
    type RegistroFormOutput,
} from "../../schemas/authSchemas";
import { parseApiError } from "../../services/api";
import { authService } from "../../services/authService";

const CAMPOS_API: (keyof RegistroFormInput)[] = [
    "nombre", "ubicacionValle", "tipoDocumento", "documento",
    "rol", "telefono", "correo", "contrasena",
];

function SignupPage() {
    const navigate = useNavigate();
    const [serverError, setServerError] = useState<string | null>(null);

    const {
        register,
        handleSubmit,
        setError,
        formState: { errors, isSubmitting },
    } = useForm<RegistroFormInput, unknown, RegistroFormOutput>({
        resolver: zodResolver(registroSchema),
        defaultValues: {
            nombre: "",
            ubicacionValle: "",
            documento: "",
            telefono: "",
            correo: "",
            contrasena: "",
            confirmarContrasena: "",
        },
    });

    const onSubmit = async (values: RegistroFormOutput) => {
        setServerError(null);

        // Estos tres campos son solo de la interfaz; el backend no los recibe.
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const { confirmarContrasena, aceptaTerminos, aceptaPrivacidad, ...datos } = values;

        try {
            await authService.register(datos);
            navigate("/login", { replace: true, state: { registered: true } });
        } catch (error) {
            const apiError = parseApiError(error);

            if (apiError.codigo === "DOCUMENTO_DUPLICADO") {
                setError("documento", { message: "Ya existe una cuenta con este documento" });
            } else if (apiError.codigo === "CORREO_DUPLICADO") {
                setError("correo", { message: "Ya existe una cuenta con este correo" });
            } else if (apiError.codigo === "VALIDACION_FALLIDA" && apiError.errores.length > 0) {
                apiError.errores.forEach(({ campo, mensaje }) => {
                    if ((CAMPOS_API as string[]).includes(campo)) {
                        setError(campo as keyof RegistroFormInput, { message: mensaje });
                    }
                });
            } else {
                setServerError(apiError.mensaje);
            }
        }
    };

    const describe = (name: keyof RegistroFormInput) => ({
        "aria-invalid": errors[name] ? (true as const) : undefined,
        "aria-describedby": errors[name] ? `${name}-error` : undefined,
    });

    return (
        <AuthLayout variant="signup">
            <div className="signup-container">
                <header className="signup-header">
                    <h1>Gracias por querer unirte a la familia AgroValle!</h1>
                    <p>Completa los campos de información, estás a solo un paso de conectar con AgroValle.</p>
                </header>

                {serverError && (
                    <p className="notice notice--error" role="alert">{serverError}</p>
                )}

                <form onSubmit={handleSubmit(onSubmit)} noValidate>
                    <div className="signup-grid">
                        <div className="signup-column">
                            <FormField id="nombre" label="Nombre" error={errors.nombre?.message}>
                                <input id="nombre" type="text" placeholder="ej: pepe perez"
                                    autoComplete="name" {...describe("nombre")} {...register("nombre")} />
                            </FormField>

                            <FormField id="ubicacionValle" label="Ubicación" error={errors.ubicacionValle?.message}>
                                <input id="ubicacionValle" type="text" placeholder="ej: El Zarzal"
                                    autoComplete="address-level2" {...describe("ubicacionValle")}
                                    {...register("ubicacionValle")} />
                            </FormField>

                            <FormField id="tipoDocumento" label="Tipo de Documento" error={errors.tipoDocumento?.message}>
                                <select id="tipoDocumento" required defaultValue="" {...describe("tipoDocumento")}
                                    {...register("tipoDocumento")}>
                                    <option value="" disabled>Selecciona una opción</option>
                                    <option value="CC">Cédula de Ciudadanía (CC)</option>
                                    <option value="CE">Cédula de Extranjería (CE)</option>
                                    <option value="PASAPORTE">Pasaporte</option>
                                </select>
                            </FormField>

                            <FormField id="documento" label="Documento" error={errors.documento?.message}>
                                <input id="documento" type="text" placeholder="ej: 123456" autoComplete="off"
                                    {...describe("documento")} {...register("documento")} />
                            </FormField>

                            <FormField id="telefono" label="Teléfono" error={errors.telefono?.message}>
                                <input id="telefono" type="tel" inputMode="tel" placeholder="ej: 1234567891"
                                    autoComplete="tel" {...describe("telefono")} {...register("telefono")} />
                            </FormField>
                        </div>

                        <div className="signup-column">
                            <FormField id="rol" label="Rol usuario" error={errors.rol?.message}>
                                <select id="rol" required defaultValue="" {...describe("rol")} {...register("rol")}>
                                    <option value="" disabled>Selecciona una opción</option>
                                    <option value="AGRICULTOR">Agricultor</option>
                                    <option value="COMPRADOR">Comprador</option>
                                </select>
                            </FormField>

                            <FormField id="correo" label="Correo" error={errors.correo?.message}>
                                <input id="correo" type="email" placeholder="ej: pepe@gmail.com"
                                    autoComplete="email" {...describe("correo")} {...register("correo")} />
                            </FormField>

                            <FormField id="contrasena" label="Contraseña" error={errors.contrasena?.message}>
                                <PasswordInput id="contrasena" placeholder="ej: tu*contraseña"
                                    autoComplete="new-password" registration={register("contrasena")}
                                    hasError={Boolean(errors.contrasena)} />
                            </FormField>

                            <FormField id="confirmarContrasena" label="Confirmar contraseña"
                                error={errors.confirmarContrasena?.message}>
                                <PasswordInput id="confirmarContrasena" placeholder="ej: tu*contraseña"
                                    autoComplete="new-password" registration={register("confirmarContrasena")}
                                    hasError={Boolean(errors.confirmarContrasena)} />
                            </FormField>
                        </div>
                    </div>

                    <div className="signup-actions">
                        <div className="signup-checks">
                            <div>
                                <label className="check-option">
                                    <input type="checkbox" aria-invalid={errors.aceptaTerminos ? true : undefined}
                                        {...register("aceptaTerminos")} />
                                    <span>Acepto los <u>Términos y condiciones</u>.</span>
                                </label>
                                {errors.aceptaTerminos && (
                                    <p className="field-error" role="alert">{errors.aceptaTerminos.message}</p>
                                )}
                            </div>
                            <div>
                                <label className="check-option">
                                    <input type="checkbox" aria-invalid={errors.aceptaPrivacidad ? true : undefined}
                                        {...register("aceptaPrivacidad")} />
                                    <span>Acepto la <u>Política de privacidad</u>.</span>
                                </label>
                                {errors.aceptaPrivacidad && (
                                    <p className="field-error" role="alert">{errors.aceptaPrivacidad.message}</p>
                                )}
                            </div>
                        </div>

                        <button type="submit" className="login-button" disabled={isSubmitting}>
                            {isSubmitting ? "Creando cuenta…" : "Crear cuenta"}
                        </button>
                    </div>
                </form>

                <p className="signup-footer">
                    ¿Ya tienes una cuenta? <Link to="/login">Inicia sesión</Link>
                </p>
            </div>
        </AuthLayout>
    );
}

export default SignupPage;
