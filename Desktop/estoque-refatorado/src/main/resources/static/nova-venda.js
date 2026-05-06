if (!requireAuth()) throw new Error('not auth');
renderNav('/nova-venda.html');

let produtos = [];
let itens = [];

function showMsg(text, type) {
    const el = document.getElementById('msg');
    el.textContent = text;
    el.className = `msg msg-${type} show`;
}

async function inicializar() {
    const [resClientes, resProdutos] = await Promise.all([
        authFetch('/api/clientes'),
        authFetch('/api/produtos')
    ]);
    if (!resClientes || !resProdutos) return;

    const clientes = await resClientes.json();
    produtos = await resProdutos.json();

    const selCliente = document.getElementById('selCliente');
    selCliente.innerHTML = '<option value="">— Sem cliente —</option>' +
        clientes.map(c => `<option value="${c.idCliente}">${escapeHtml(c.nome)}</option>`).join('');

    const selProduto = document.getElementById('selProduto');
    selProduto.innerHTML = '<option value="">Selecione um produto</option>' +
        produtos.map(p => `<option value="${p.idProduto}" data-preco="${p.preco}">${escapeHtml(p.nome)} — R$ ${(p.preco ?? 0).toFixed(2)}</option>`).join('');
}

function preencherPreco() {
    const sel = document.getElementById('selProduto');
    const opt = sel.options[sel.selectedIndex];
    const preco = opt ? parseFloat(opt.dataset.preco || 0) : 0;
    document.getElementById('precoUnit').value = preco > 0 ? preco.toFixed(2) : '';
}

function adicionarItem() {
    const selProduto = document.getElementById('selProduto');
    const idProduto = parseInt(selProduto.value);
    const quantidade = parseFloat(document.getElementById('quantidade').value);
    const precoUnitario = parseFloat(document.getElementById('precoUnit').value);

    if (!idProduto) { showMsg('Selecione um produto.', 'error'); return; }
    if (!quantidade || quantidade <= 0) { showMsg('Informe uma quantidade valida.', 'error'); return; }
    if (isNaN(precoUnitario) || precoUnitario <= 0) { showMsg('Informe um preco unitario valido (maior que zero).', 'error'); return; }

    const produto = produtos.find(p => p.idProduto === idProduto);
    if (!produto) return;

    const existente = itens.find(i => i.produto.idProduto === idProduto);
    if (existente) {
        existente.quantidade += quantidade;
        existente.precoUnitario = precoUnitario;
    } else {
        itens.push({ produto, quantidade, precoUnitario });
    }

    selProduto.value = '';
    document.getElementById('quantidade').value = '';
    document.getElementById('precoUnit').value = '';

    const msgEl = document.getElementById('msg');
    if (msgEl.classList.contains('msg-error')) {
        msgEl.className = 'msg';
    }

    renderItens();
}

function removerItem(index) {
    itens.splice(index, 1);
    renderItens();
}

function renderItens() {
    const tbody = document.getElementById('tabelaItens');
    const totalEl = document.getElementById('totalVenda');

    if (itens.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="empty-state">Nenhum item adicionado.</td></tr>';
        totalEl.textContent = 'R$ 0.00';
        return;
    }

    let total = 0;
    tbody.innerHTML = itens.map((item, idx) => {
        const subtotal = item.quantidade * item.precoUnitario;
        total += subtotal;
        return `
            <tr>
                <td>${escapeHtml(item.produto.nome)}</td>
                <td>${item.quantidade}</td>
                <td>R$ ${item.precoUnitario.toFixed(2)}</td>
                <td><span class="badge badge-success">R$ ${subtotal.toFixed(2)}</span></td>
                <td><button class="btn btn-danger btn-sm" onclick="removerItem(${idx})">Remover</button></td>
            </tr>
        `;
    }).join('');

    totalEl.textContent = `R$ ${total.toFixed(2)}`;
}

async function finalizarVenda() {
    if (itens.length === 0) { showMsg('Adicione pelo menos um item a venda.', 'error'); return; }

    const clienteId = document.getElementById('selCliente').value;
    const funcionario = getFuncionario();

    const payload = {
        itens: itens.map(i => ({
            produto: { idProduto: i.produto.idProduto },
            quantidade: i.quantidade,
            precoUnitario: i.precoUnitario
        }))
    };

    if (clienteId) payload.cliente = { idCliente: parseInt(clienteId) };
    if (funcionario?.id) payload.funcionario = { idFuncionario: funcionario.id };

    const btn = document.getElementById('btnFinalizar');
    btn.disabled = true;
    btn.textContent = 'Processando...';

    const res = await authFetch('/api/vendas', { method: 'POST', body: JSON.stringify(payload) });

    btn.disabled = false;
    btn.textContent = 'Finalizar Venda';

    if (!res) return;

    if (res.ok) {
        const venda = await res.json();
        showMsg(`Venda #${venda.idVenda} registrada! Total: R$ ${(venda.valorTotal ?? 0).toFixed(2)}`, 'success');
        itens = [];
        renderItens();
        document.getElementById('selCliente').value = '';
    } else {
        const err = await res.json().catch(() => ({}));
        showMsg(err.erro || err.message || 'Erro ao registrar venda.', 'error');
    }
}

document.getElementById('selProduto').addEventListener('change', preencherPreco);
document.getElementById('btnAdicionarItem').addEventListener('click', adicionarItem);
document.getElementById('btnFinalizar').addEventListener('click', finalizarVenda);

document.getElementById('quantidade').addEventListener('keydown', e => {
    if (e.key === 'Enter') adicionarItem();
});
document.getElementById('precoUnit').addEventListener('keydown', e => {
    if (e.key === 'Enter') adicionarItem();
});

inicializar();
renderItens();
