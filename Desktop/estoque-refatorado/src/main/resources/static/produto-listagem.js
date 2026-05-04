if (!requireAuth()) throw new Error('not auth');
renderNav('/produto-listagem.html');

function statusVencimento(dataVenc) {
    if (!dataVenc) return '';
    const hoje = new Date();
    const venc  = new Date(dataVenc);
    const diff  = (venc - hoje) / (1000 * 60 * 60 * 24);
    if (diff < 0)  return '<span class="badge badge-danger">Vencido</span>';
    if (diff <= 7) return '<span class="badge badge-warning">Vence em breve</span>';
    return '<span class="badge badge-success">OK</span>';
}

async function carregarProdutos() {
    const busca = document.getElementById('busca').value.trim();
    const url   = busca ? `/api/produtos?nome=${encodeURIComponent(busca)}` : '/api/produtos';
    const res   = await authFetch(url);
    if (!res) return;
    const produtos = await res.json();
    const tbody = document.getElementById('tabelaProdutos');

    if (produtos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="empty-state">Nenhum produto encontrado.</td></tr>';
        return;
    }

    tbody.innerHTML = produtos.map(p => `
        <tr>
            <td><strong>${toSafeText(p.nome)}</strong></td>
            <td>${toSafeText(p.tipo)}</td>
            <td>R$ ${parseFloat(p.preco).toFixed(2)}</td>
            <td>${p.quantidade <= 5
                ? `<span class="badge badge-danger">${p.quantidade}</span>`
                : `<span class="badge badge-success">${p.quantidade}</span>`}
            </td>
            <td>${toSafeText(p.dataVencimento ?? '—')}</td>
            <td>${statusVencimento(p.dataVencimento)}</td>
            <td>
                <button class="btn btn-danger btn-sm" onclick="deletar(${p.idProduto})">Excluir</button>
            </td>
        </tr>
    `).join('');
}

async function deletar(id) {
    if (!confirm('Confirmar exclusão do produto?')) return;
    const res = await authFetch(`/api/produtos/${id}`, { method: 'DELETE' });
    if (res) carregarProdutos();
}

document.getElementById('busca').addEventListener('keydown', e => {
    if (e.key === 'Enter') carregarProdutos();
});

carregarProdutos();
