if (!requireAuth()) throw new Error('not auth');
renderNav('/image-gallery.html');

async function carregarImagens() {
    const res = await authFetch('/api/imagens');
    if (!res) return;
    const imagens = await res.json();
    const galeria = document.getElementById('galeria');

    if (imagens.length === 0) {
        galeria.innerHTML = '<p class="empty-state">Nenhuma imagem disponível.</p>';
        return;
    }

    galeria.innerHTML = imagens.map(src => `
        <img src="${src}" alt="Produto" onerror="this.style.display='none'">
    `).join('');
}

carregarImagens();
