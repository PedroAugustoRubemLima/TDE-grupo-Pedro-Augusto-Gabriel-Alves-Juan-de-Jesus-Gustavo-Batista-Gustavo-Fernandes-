if (!requireAuth()) throw new Error('not auth');
renderNav('/retira-estoque.html');

function showMsg(id, text, type) {
    const el = document.getElementById(id);
    el.textContent = text;
    el.className = `msg msg-${type} show`;
    setTimeout(() => el.classList.remove('show'), 4000);
}

async function carregarProdutos() {
    const res = await authFetch('/api/produtos');
    if (!res) return;
    const produtos = await res.json();
    const opts = produtos.map(p => `<option value="${p.idProduto}">${p.nome} (${p.quantidade} un)</option>`).join('');
    document.getElementById('produtoRetirar').innerHTML = opts;
    document.getElementById('produtoAdicionar').innerHTML = opts;
}

async function carregarEstoque() {
    const res = await authFetch('/api/estoque');
    if (!res) return;
    const estoque = await res.json();
    const tbody = document.getElementById('tabelaEstoque');
    if (estoque.length === 0) {
        tbody.innerHTML = '<tr><td colspan="3" class="empty-state">Nenhum item no estoque.</td></tr>';
        return;
    }
    tbody.innerHTML = estoque.map(e => `
        <tr>
            <td>${e.produto?.nome ?? '—'}</td>
            <td>${e.produto?.tipo ?? '—'}</td>
            <td>${e.quantidadeAtual <= 5
                ? `<span class="badge badge-danger">${e.quantidadeAtual}</span>`
                : `<span class="badge badge-success">${e.quantidadeAtual}</span>`}
            </td>
        </tr>
    `).join('');
}

document.getElementById('btnRetirar').addEventListener('click', async () => {
    const produtoId = document.getElementById('produtoRetirar').value;
    const quantidade = document.getElementById('qtdRetirar').value;
    if (!produtoId || !quantidade) { showMsg('msgRetirar', 'Preencha todos os campos.', 'error'); return; }

    const res = await authFetch(`/api/estoque/retirar?produtoId=${produtoId}&quantidade=${quantidade}`, { method: 'POST' });
    if (!res) return;

    if (res.ok) {
        showMsg('msgRetirar', 'Estoque atualizado com sucesso!', 'success');
        document.getElementById('qtdRetirar').value = '';
        await carregarProdutos();
        await carregarEstoque();
    } else {
        const err = await res.json();
        showMsg('msgRetirar', err.erro || 'Erro ao retirar.', 'error');
    }
});

document.getElementById('btnAdicionar').addEventListener('click', async () => {
    const produtoId = document.getElementById('produtoAdicionar').value;
    const quantidade = document.getElementById('qtdAdicionar').value;
    if (!produtoId || !quantidade) { showMsg('msgAdicionar', 'Preencha todos os campos.', 'error'); return; }

    const res = await authFetch(`/api/estoque/adicionar?produtoId=${produtoId}&quantidade=${quantidade}`, { method: 'POST' });
    if (!res) return;

    if (res.ok) {
        showMsg('msgAdicionar', 'Estoque adicionado com sucesso!', 'success');
        document.getElementById('qtdAdicionar').value = '';
        await carregarProdutos();
        await carregarEstoque();
    } else {
        const err = await res.json();
        showMsg('msgAdicionar', err.erro || 'Erro ao adicionar.', 'error');
    }
});

carregarProdutos();
carregarEstoque();
