if (!requireAuth()) throw new Error('not auth');
renderNav('/cliente-cadastro.html');

function showMsg(text, type) {
    const el = document.getElementById('msg');
    el.textContent = text;
    el.className = `msg msg-${type} show`;
}

async function carregarClientes() {
    const res = await authFetch('/api/clientes');
    if (!res) return;
    const clientes = await res.json();
    const tbody = document.getElementById('tabelaClientes');
    if (clientes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="empty-state">Nenhum cliente.</td></tr>';
        return;
    }
    tbody.innerHTML = clientes.map(c => `
        <tr>
            <td>${toSafeText(c.nome)}</td>
            <td>${toSafeText(c.telefone ?? '—')}</td>
            <td>
                <button class="btn btn-danger btn-sm" onclick="deletar(${c.idCliente})">Excluir</button>
            </td>
        </tr>
    `).join('');
}

async function deletar(id) {
    if (!confirm('Confirmar exclusão?')) return;
    const res = await authFetch(`/api/clientes/${id}`, { method: 'DELETE' });
    if (res) carregarClientes();
}

document.getElementById('btnSalvar').addEventListener('click', async () => {
    const nome = document.getElementById('nome').value.trim();
    if (!nome) { showMsg('Nome é obrigatório.', 'error'); return; }

    const cliente = {
        nome,
        cpf:      document.getElementById('cpf').value.trim() || null,
        telefone: document.getElementById('telefone').value.trim() || null,
        email:    document.getElementById('email').value.trim() || null,
    };

    const btn = document.getElementById('btnSalvar');
    btn.disabled = true;

    const res = await authFetch('/api/clientes', { method: 'POST', body: JSON.stringify(cliente) });
    btn.disabled = false;
    if (!res) return;

    if (res.ok) {
        showMsg('Cliente cadastrado com sucesso!', 'success');
        document.querySelectorAll('input').forEach(i => i.value = '');
        carregarClientes();
    } else {
        const err = await res.json();
        showMsg(err.erro || 'Erro ao salvar.', 'error');
    }
});

carregarClientes();
