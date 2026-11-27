
import { api } from "./api";

import type {
    LoginRequest,
    LoginResponse,
    RegistroRequest,
    RegistroResponse,
} from "../types/auth";

export const authService = {
    async login(data: LoginRequest): Promise<LoginResponse> {
        const response = await api.post<LoginResponse>(
        "/login",
        data
    );

        return response.data;
    },

    async register(data: RegistroRequest): Promise<RegistroResponse> {
        const response = await api.post<RegistroResponse>(
        "/register",
        data
    );

        return response.data;
    },
};
