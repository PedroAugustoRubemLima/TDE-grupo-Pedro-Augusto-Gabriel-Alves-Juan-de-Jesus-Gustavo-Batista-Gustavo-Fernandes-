// Se existir token, valida no backend antes de redirecionar.
(async function validarSessaoAtiva() {
    const token = getToken() || localStorage.getItem('jwt_token');
    if (!token) return;

    try {
        const res = await fetch(API + '/api/auth/validate', {
            headers: { 'Authorization': 'Bearer ' + token }
        });

        if (!res.ok) {
            logout();
            return;
        }

        const funcionario = await res.json();
        sessionStorage.setItem('jwt_token', token);
        sessionStorage.setItem('funcionario', JSON.stringify(funcionario));
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('funcionario');
        window.location.href = '/menu.html';
    } catch (_) {
        logout();
    }
})();

// Evita reaproveitamento de valores preenchidos automaticamente.
window.addEventListener('pageshow', () => {
    const usuarioEl = document.getElementById('usuario');
    const senhaEl = document.getElementById('senha');
    if (usuarioEl) usuarioEl.value = '';
    if (senhaEl) senhaEl.value = '';
});

function showMsg(text, type) {
    const el = document.getElementById('msg');
    el.textContent = text;
    el.className = `msg msg-${type} show`;
}

async function doLogin() {
    const usuario = document.getElementById('usuario').value.trim();
    const senha   = document.getElementById('senha').value;
    const btn     = document.getElementById('btnLogin');

    if (!usuario || !senha) { showMsg('Preencha usuário e senha.', 'error'); return; }

    btn.disabled = true;
    btn.textContent = 'Entrando...';

    try {
        const res = await fetch(API + '/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usuario, senha })
        });

        const data = await res.json();

        if (!res.ok) {
            showMsg(data.erro || 'Credenciais inválidas.', 'error');
            return;
        }

        sessionStorage.setItem('jwt_token', data.token);
        sessionStorage.setItem('funcionario', JSON.stringify(data.funcionario));
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('funcionario');
        window.location.href = '/menu.html';

    } catch (e) {
        showMsg('Erro de conexão com o servidor.', 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Entrar';
    }
}

document.getElementById('btnLogin').addEventListener('click', doLogin);
document.addEventListener('keydown', e => { if (e.key === 'Enter') doLogin(); });
