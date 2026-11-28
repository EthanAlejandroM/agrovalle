const KEY = "agrovalle.token";

/** "Recuérdame" => localStorage (persiste); si no, sessionStorage (se borra al cerrar). */
export function saveToken(token: string, remember: boolean): void {
    clearToken();
    (remember ? localStorage : sessionStorage).setItem(KEY, token);
}

export function getToken(): string | null {
    return localStorage.getItem(KEY) ?? sessionStorage.getItem(KEY);
}

export function clearToken(): void {
    localStorage.removeItem(KEY);
    sessionStorage.removeItem(KEY);
}

export interface Session {
    correo: string;
    rol: string;
}

/** Lee correo y rol del JWT solo para mostrarlos; la seguridad real la valida el backend. */
export function getSession(): Session | null {
    const token = getToken();
    if (!token) return null;

    try {
        const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
        const json = new TextDecoder().decode(
            Uint8Array.from(atob(base64), (c) => c.charCodeAt(0))
        );
        const payload = JSON.parse(json) as { sub: string; role: string; exp?: number };

        if (payload.exp && payload.exp * 1000 < Date.now()) {
            clearToken();
            return null;
        }
        return { correo: payload.sub, rol: payload.role };
    } catch {
        clearToken();
        return null;
    }
}
