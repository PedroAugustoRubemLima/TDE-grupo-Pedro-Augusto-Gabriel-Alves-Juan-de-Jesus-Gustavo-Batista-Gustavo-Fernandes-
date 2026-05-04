if (!requireAuth()) throw new Error('not auth');
renderNav('/historico-vendas.html');

async function carregarVendas(url = '/api/vendas') {
    const res = await authFetch(url);
    if (!res) return;
    const vendas = await res.json();
    const tbody = document.getElementById('tabelaVendas');

    if (vendas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="empty-state">Nenhuma venda encontrada.</td></tr>';
        return;
    }

    tbody.innerHTML = vendas.map(v => `
        <tr>
            <td>#${v.idVenda}</td>
            <td>${v.dataVenda ?? '—'}</td>
            <td>${v.cliente?.nome ?? '—'}</td>
            <td>${v.funcionario?.nome ?? '—'}</td>
            <td><span class="badge badge-success">R$ ${(v.valorTotal ?? 0).toFixed(2)}</span></td>
            <td><span class="badge badge-info">${v.itens?.length ?? 0} item(s)</span></td>
        </tr>
    `).join('');
}

async function filtrar() {
    const inicio = document.getElementById('dataInicio').value;
    const fim    = document.getElementById('dataFim').value;
    if (!inicio || !fim) { alert('Selecione as duas datas.'); return; }
    await carregarVendas(`/api/vendas/periodo?inicio=${inicio}&fim=${fim}`);
}

async function limparFiltro() {
    document.getElementById('dataInicio').value = '';
    document.getElementById('dataFim').value = '';
    await carregarVendas();
}

async function gerarPDF() {
    const inicio = document.getElementById('dataInicio').value;
    const fim    = document.getElementById('dataFim').value;
    const url    = inicio && fim
        ? `/api/vendas/relatorio/pdf?inicio=${inicio}&fim=${fim}`
        : '/api/vendas/relatorio/pdf';

    const res = await authFetch(url);
    if (!res || !res.ok) { alert('Erro ao gerar PDF.'); return; }

    const blob = await res.blob();
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'relatorio-vendas.pdf';
    link.click();
}

carregarVendas();
