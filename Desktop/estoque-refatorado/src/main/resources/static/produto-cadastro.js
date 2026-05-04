if (!requireAuth()) throw new Error('not auth');
renderNav('/produto-cadastro.html');

function showMsg(text, type) {
    const el = document.getElementById('msg');
    el.textContent = text;
    el.className = `msg msg-${type} show`;
}

document.getElementById('btnSalvar').addEventListener('click', async () => {
    const nome        = document.getElementById('nome').value.trim();
    const tipo        = document.getElementById('tipo').value.trim();
    const preco       = document.getElementById('preco').value;
    const quantidade  = document.getElementById('quantidade').value;
    const dataVenc    = document.getElementById('dataVencimento').value;

    if (!nome || !tipo || !preco || !quantidade) {
        showMsg('Preencha todos os campos obrigatórios (*).', 'error');
        return;
    }

    const produto = { nome, tipo, preco: parseFloat(preco), quantidade: parseFloat(quantidade) };
    if (dataVenc) produto.dataVencimento = dataVenc;

    const btn = document.getElementById('btnSalvar');
    btn.disabled = true;
    btn.textContent = 'Salvando...';

    try {
        const res = await authFetch('/api/produtos', {
            method: 'POST',
            body: JSON.stringify(produto)
        });

        if (!res) return;

        if (res.ok) {
            showMsg('Produto cadastrado com sucesso!', 'success');
            document.querySelectorAll('input').forEach(i => i.value = '');
        } else {
            const err = await res.json();
            showMsg(err.erro || JSON.stringify(err.campos) || 'Erro ao salvar.', 'error');
        }
    } finally {
        btn.disabled = false;
        btn.textContent = 'Salvar Produto';
    }
});
