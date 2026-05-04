if (!requireAuth()) throw new Error('not auth');
renderNav('/funcionario-cadastro.html');

function showMsg(text, type) {
    const el = document.getElementById('msg');
    el.textContent = text;
    el.className = `msg msg-${type} show`;
}

async function carregarFuncionarios() {
    const res = await authFetch('/api/funcionarios');
    if (!res) return;
    const lista = await res.json();
    const tbody = document.getElementById('tabelaFuncionarios');
    if (lista.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="empty-state">Nenhum funcionário.</td></tr>';
        return;
    }
    tbody.innerHTML = lista.map(f => `
        <tr>
            <td>${toSafeText(f.nome)}</td>
            <td><span class="badge badge-info">${toSafeText(f.cargo)}</span></td>
            <td>${toSafeText(f.usuario)}</td>
            <td>
                <button class="btn btn-danger btn-sm" onclick="deletar(${f.idFuncionario})">Excluir</button>
            </td>
        </tr>
    `).join('');
}

async function deletar(id) {
    if (!confirm('Confirmar exclusão do funcionário?')) return;
    const res = await authFetch(`/api/funcionarios/${id}`, { method: 'DELETE' });
    if (res) carregarFuncionarios();
}

document.getElementById('btnSalvar').addEventListener('click', async () => {
    const nome    = document.getElementById('nome').value.trim();
    const cargo   = document.getElementById('cargo').value.trim();
    const usuario = document.getElementById('usuario').value.trim();
    const senha   = document.getElementById('senha').value;

    if (!nome || !cargo || !usuario || !senha) {
        showMsg('Preencha todos os campos obrigatórios (*).', 'error');
        return;
    }

    const funcionario = {
        nome, cargo, usuario, senha,
        cpf: document.getElementById('cpf').value.trim() || null
    };

    const btn = document.getElementById('btnSalvar');
    btn.disabled = true;

    const res = await authFetch('/api/funcionarios', { method: 'POST', body: JSON.stringify(funcionario) });
    btn.disabled = false;
    if (!res) return;

    if (res.ok) {
        showMsg('Funcionário cadastrado com sucesso!', 'success');
        document.querySelectorAll('input').forEach(i => i.value = '');
        carregarFuncionarios();
    } else {
        const err = await res.json();
        showMsg(err.erro || 'Erro ao salvar.', 'error');
    }
});

carregarFuncionarios();
