
import { z } from "zod";

export const loginSchema = z.object({
    correo: z
        .string()
        .min(1, "El correo es obligatorio")
        .email("Ingresa un correo válido"),

    contrasena: z
        .string()
        .min(1, "La contraseña es obligatoria"),
});

export const registroSchema = z
    .object({
        nombre: z.string().trim().min(1, "El nombre es obligatorio"),

        ubicacionValle: z
        .string()
        .trim()
        .min(1, "La ubicación es obligatoria"),

        tipoDocumento: z.enum(["CC", "CE", "PASAPORTE"], {
        error: "Selecciona un tipo de documento",
        }),

        documento: z
        .string()
        .regex(
            /^[A-Za-z0-9]+$/,
            "Utiliza únicamente letras y números, sin espacios ni guiones"
        ),

        rol: z.enum(["AGRICULTOR", "COMPRADOR"], {
        error: "Selecciona un rol",
        }),

        telefono: z.string().min(1, "El teléfono es obligatorio"),

        correo: z
        .string()
        .min(1, "El correo es obligatorio")
        .email("Ingresa un correo válido"),

        contrasena: z
        .string()
        .min(8, "La contraseña debe tener al menos 8 caracteres"),

        confirmarContrasena: z
        .string()
        .min(1, "Confirma tu contraseña"),

        aceptaTerminos: z.literal(true, {
        error: "Debes aceptar los términos y condiciones",
        }),

        aceptaPrivacidad: z.literal(true, {
        error: "Debes aceptar la política de privacidad",
        }),
    })
    .refine(
        (data) => data.contrasena === data.confirmarContrasena,
        {
        message: "Las contraseñas no coinciden",
        path: ["confirmarContrasena"],
        }
    );
