import axios from "axios";

import type { ApiErrorResponse } from "../types/auth";

// Vacío => mismo origen (proxy de Vite en local, rewrite de Render en producción).
const API_URL = (import.meta.env.VITE_API_URL ?? "").replace(/\/$/, "");

export const api = axios.create({
    baseURL: `${API_URL}/api/v1/auth`,
    headers: {
        "Content-Type": "application/json",
    },
});

export interface ApiErrorInfo {
    status: number;
    codigo?: string;
    mensaje: string;
    errores: { campo: string; mensaje: string }[];
}

/** Convierte cualquier error de axios en un objeto simple y legible para la UI. */
export function parseApiError(error: unknown): ApiErrorInfo {
    if (axios.isAxiosError<ApiErrorResponse>(error)) {
        if (!error.response) {
            return {
                status: 0,
                codigo: "SIN_CONEXION",
                mensaje:
                    "No pudimos conectarnos con el servidor. Revisa tu conexión e inténtalo de nuevo.",
                errores: [],
            };
        }

        const data = error.response.data;
        return {
            status: error.response.status,
            codigo: data?.codigo,
            mensaje: data?.detalle ?? "Ocurrió un error inesperado.",
            errores: data?.errores ?? [],
        };
    }

    return { status: 0, mensaje: "Ocurrió un error inesperado.", errores: [] };
}
