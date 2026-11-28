import { useState } from "react";
import type { UseFormRegisterReturn } from "react-hook-form";
import { Eye, EyeOff } from "lucide-react";

interface PasswordInputProps {
    id: string;
    placeholder: string;
    autoComplete: string;
    registration: UseFormRegisterReturn;
    hasError?: boolean;
}

/** Campo de contraseña con el ojo para mostrar/ocultar (login y registro). */
function PasswordInput({ id, placeholder, autoComplete, registration, hasError }: PasswordInputProps) {
    const [visible, setVisible] = useState(false);

    return (
        <div className="password-wrapper">
            <input
                id={id}
                type={visible ? "text" : "password"}
                placeholder={placeholder}
                autoComplete={autoComplete}
                aria-invalid={hasError || undefined}
                aria-describedby={hasError ? `${id}-error` : undefined}
                {...registration}
            />
            <button
                type="button"
                className="password-toggle"
                onClick={() => setVisible((v) => !v)}
                aria-label={visible ? "Ocultar contraseña" : "Mostrar contraseña"}
                aria-pressed={visible}
            >
                {visible ? <EyeOff size={24} strokeWidth={2.5} /> : <Eye size={24} strokeWidth={2.5} />}
            </button>
        </div>
    );
}

export default PasswordInput;
