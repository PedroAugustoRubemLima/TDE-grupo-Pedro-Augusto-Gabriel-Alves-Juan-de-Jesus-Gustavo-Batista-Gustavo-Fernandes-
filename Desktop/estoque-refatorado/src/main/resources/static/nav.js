// nav.js — Renderiza o menu de navegação em todas as páginas
function renderNav(activePage) {
    const links = [
        { href: '/menu.html',                 label: 'Dashboard' },
        { href: '/produto-listagem.html',     label: 'Produtos' },
        { href: '/produto-cadastro.html',     label: 'Cadastrar Produto' },
        { href: '/retira-estoque.html',       label: 'Estoque' },
        { href: '/historico-vendas.html',     label: 'Vendas' },
        { href: '/cliente-cadastro.html',     label: 'Clientes' },
        { href: '/funcionario-cadastro.html', label: 'Funcionarios' },
        { href: '/image-gallery.html',        label: 'Galeria' },
    ];

    const linksHtml = links.map(l =>
        `<li><a href="${l.href}" class="${activePage === l.href ? 'active' : ''}">${l.label}</a></li>`
    ).join('');

    document.body.insertAdjacentHTML('afterbegin', `
        <nav>
            <a href="/menu.html" class="nav-logo">
                <span class="nav-logo-mark">JB</span>
                JB Estoque
            </a>
            <ul class="nav-links">${linksHtml}</ul>
            <div class="nav-right">
                <span id="user-info"></span>
                <button class="btn-logout" onclick="logout()">Sair</button>
            </div>
        </nav>
    `);

    renderUserInfo();
}
