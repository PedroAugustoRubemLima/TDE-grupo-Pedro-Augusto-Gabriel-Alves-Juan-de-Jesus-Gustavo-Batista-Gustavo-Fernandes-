// ============================================================
// auth.js — Helpers de autenticação JWT compartilhado
// Inclua em todas as páginas protegidas
// ============================================================

const API = 'http://localhost:8080';
const TOKEN_KEY = 'jwt_token';
const USER_KEY = 'funcionario';

function getToken() {
    return sessionStorage.getItem(TOKEN_KEY);
}

function getFuncionario() {
    const raw = sessionStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
}

function logout() {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
    // Remove legados salvos em localStorage de versões antigas.
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    window.location.href = '/login.html';
}

// Adiciona Authorization header em todas as requisições autenticadas
function authHeaders() {
    const csrfToken = getCookie('XSRF-TOKEN');
    const headers = {
        'Authorization': 'Bearer ' + getToken()
    };
    if (csrfToken) {
        headers['X-XSRF-TOKEN'] = csrfToken;
    }
    return headers;
}

function getCookie(name) {
    const key = name + '=';
    const cookies = document.cookie ? document.cookie.split(';') : [];
    for (const rawCookie of cookies) {
        const cookie = rawCookie.trim();
        if (cookie.startsWith(key)) {
            return decodeURIComponent(cookie.substring(key.length));
        }
    }
    return null;
}

function escapeHtml(value) {
    if (value == null) return '';
    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}

function toSafeText(value) {
    return escapeHtml(value);
}

async function authFetch(url, options = {}) {
    const method = (options.method || 'GET').toUpperCase();
    const defaultHeaders = authHeaders();
    if (method !== 'GET' && method !== 'HEAD' && method !== 'OPTIONS') {
        defaultHeaders['Content-Type'] = 'application/json';
    }
    options.headers = { ...defaultHeaders, ...(options.headers || {}) };
    const res = await fetch(API + url, options);
    if (res.status === 401) {
        logout();
        return null;
    }
    return res;
}

// Redireciona para login se não tiver token
function requireAuth() {
    if (!getToken()) {
        window.location.href = '/login.html';
        return false;
    }
    return true;
}

// Renderiza info do usuário logado no nav
function renderUserInfo() {
    const f = getFuncionario();
    if (!f) return;
    const el = document.getElementById('user-info');
    if (el) el.textContent = `${f.nome} · ${f.cargo}`;
}
