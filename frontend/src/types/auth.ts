
export type TipoDocumento = "CC" | "CE" | "PASAPORTE";

export type RolUsuario = "AGRICULTOR" | "COMPRADOR";

export interface LoginRequest {
    correo: string;
    contrasena: string;
}

export interface LoginResponse {
    accessToken: string;
    tokenType: string;
    expiresIn: number;
}

export interface RegistroRequest {
    nombre: string;
    ubicacionValle: string;
    tipoDocumento: TipoDocumento;
    documento: string;
    rol: RolUsuario;
    telefono: string;
    correo: string;
    contrasena: string;
}

export interface RegistroResponse {
    id: number;
    rol: RolUsuario;
}
