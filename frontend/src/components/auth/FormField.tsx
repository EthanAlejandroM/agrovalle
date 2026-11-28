import type { ReactNode } from "react";

interface FormFieldProps {
    id: string;
    label: string;
    error?: string;
    children: ReactNode;
}

/** Etiqueta + control + mensaje de error accesible. */
function FormField({ id, label, error, children }: FormFieldProps) {
    return (
        <div className={`form-group${error ? " form-group--error" : ""}`}>
            <label htmlFor={id}>{label}</label>
            {children}
            {error && (
                <p id={`${id}-error`} className="field-error" role="alert">
                    {error}
                </p>
            )}
        </div>
    );
}

export default FormField;
