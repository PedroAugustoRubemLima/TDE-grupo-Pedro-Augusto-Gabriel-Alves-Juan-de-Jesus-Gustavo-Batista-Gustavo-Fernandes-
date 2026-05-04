if (!requireAuth()) throw new Error('not auth');
renderNav('/menu.html');

async function loadDashboard() {
    const [produtos, clientes, vendas, baixo] = await Promise.all([
        authFetch('/api/produtos').then(r => r?.json() ?? []),
        authFetch('/api/clientes').then(r => r?.json() ?? []),
        authFetch('/api/vendas').then(r => r?.json() ?? []),
        authFetch('/api/produtos?estoqueMinimo=5').then(r => r?.json() ?? []),
    ]);

    document.getElementById('totalProdutos').textContent = produtos.length;
    document.getElementById('totalClientes').textContent = clientes.length;
    document.getElementById('totalVendas').textContent   = vendas.length;
    document.getElementById('estoqueBaixo').textContent  = baixo.length;

    const tabelaVendas = document.getElementById('tabelaVendas');
    const ultimas = [...vendas].reverse().slice(0, 8);
    if (ultimas.length === 0) {
        tabelaVendas.innerHTML = '<tr><td colspan="4" class="empty-state">Nenhuma venda registrada.</td></tr>';
    } else {
        tabelaVendas.innerHTML = ultimas.map(v => `
            <tr>
                <td>#${v.idVenda}</td>
                <td>${toSafeText(v.dataVenda ?? '—')}</td>
                <td>${toSafeText(v.cliente?.nome ?? '—')}</td>
                <td><span class="badge badge-success">R$ ${(v.valorTotal ?? 0).toFixed(2)}</span></td>
            </tr>
        `).join('');
    }

    const tabelaBaixo = document.getElementById('tabelaEstoqueBaixo');
    if (baixo.length === 0) {
        tabelaBaixo.innerHTML = '<tr><td colspan="4" class="empty-state">Nenhum produto com estoque baixo.</td></tr>';
    } else {
        tabelaBaixo.innerHTML = baixo.map(p => `
            <tr>
                <td>${toSafeText(p.nome)}</td>
                <td>${toSafeText(p.tipo)}</td>
                <td><span class="badge badge-danger">${p.quantidade}</span></td>
                <td>${toSafeText(p.dataVencimento ?? '—')}</td>
            </tr>
        `).join('');
    }
}

loadDashboard();
