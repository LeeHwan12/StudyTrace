import { apiFetch } from "./api";

export async function login({username,password}) {
    return apiFetch("/api/auth/login", {
        method: "POST",
        body: { username, password },
    });
}

export async function logout() {
    return apiFetch('/api/auth/logout',{
        method : "POST"
    });
}

export async function register({username,password,passwordConfirm,email,nickname,}) {
    return apiFetch('/api/auth/register',{
        method : "POST",
        body: {
            username,password,passwordConfirm,email,nickname
        }
    });
}

export async function me() {
    return apiFetch('/api/auth/me');
}