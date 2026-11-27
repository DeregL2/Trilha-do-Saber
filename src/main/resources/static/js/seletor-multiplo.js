// Seletor múltiplo com busca.
// Transforma uma lista de checkboxes (marcada com data-seletor-multiplo) em um campo:
// ao clicar mostra todas as opções, filtra pelo que for digitado e exibe as escolhidas como etiquetas.
// Os checkboxes originais continuam no formulário (escondidos), então o envio funciona igual a antes.
// Sem JavaScript, a lista de checkboxes aparece normalmente.
(function () {
    'use strict';

    function normalizar(texto) {
        return (texto || '').normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase().trim();
    }

    function criar(tag, classe, texto) {
        var el = document.createElement(tag);
        if (classe) el.className = classe;
        if (texto !== undefined) el.textContent = texto;
        return el;
    }

    function montar(lista, indice) {
        var opcoes = Array.prototype.map.call(lista.querySelectorAll('[data-opcao]'), function (rotulo) {
            var checkbox = rotulo.querySelector('input[type="checkbox"]');
            return {
                checkbox: checkbox,
                nome: rotulo.getAttribute('data-nome') || '',
                detalhe: rotulo.getAttribute('data-detalhe') || '',
                busca: normalizar(rotulo.getAttribute('data-busca') || rotulo.textContent)
            };
        }).filter(function (o) { return o.checkbox; });

        if (!opcoes.length) return;

        var idLista = 'seletor-multiplo-' + indice;
        var raiz = criar('div', 'sm');
        var campo = criar('div', 'sm-campo');
        var etiquetas = criar('div', 'sm-etiquetas');
        var busca = criar('input', 'sm-busca');
        var painel = criar('ul', 'sm-lista');
        var vazio = criar('li', 'sm-vazio', 'Nenhum professor encontrado');
        var contador = criar('span', 'sm-contador');

        busca.type = 'text';
        busca.autocomplete = 'off';
        busca.placeholder = lista.getAttribute('data-placeholder') || 'Buscar...';
        busca.setAttribute('role', 'combobox');
        busca.setAttribute('aria-expanded', 'false');
        busca.setAttribute('aria-controls', idLista);
        busca.setAttribute('aria-autocomplete', 'list');
        if (lista.getAttribute('aria-label')) busca.setAttribute('aria-label', lista.getAttribute('aria-label'));

        painel.id = idLista;
        painel.setAttribute('role', 'listbox');
        painel.setAttribute('aria-multiselectable', 'true');
        painel.hidden = true;
        vazio.hidden = true;

        var ativa = -1;
        var itens = opcoes.map(function (opcao, i) {
            var item = criar('li', 'sm-opcao');
            item.id = idLista + '-op-' + i;
            item.setAttribute('role', 'option');
            var marca = criar('span', 'sm-marca');
            marca.setAttribute('aria-hidden', 'true');
            var textos = criar('span', 'sm-textos');
            textos.appendChild(criar('span', 'sm-nome', opcao.nome));
            if (opcao.detalhe) textos.appendChild(criar('span', 'sm-detalhe', opcao.detalhe));
            item.appendChild(marca);
            item.appendChild(textos);
            // mousedown + preventDefault mantém o foco na busca enquanto marca a opção
            item.addEventListener('mousedown', function (e) {
                e.preventDefault();
                alternar(i);
            });
            painel.appendChild(item);
            return item;
        });
        painel.appendChild(vazio);

        campo.appendChild(etiquetas);
        campo.appendChild(busca);
        raiz.appendChild(campo);
        raiz.appendChild(painel);
        raiz.appendChild(contador);

        lista.classList.add('sm-original');
        lista.parentNode.insertBefore(raiz, lista);

        function visiveis() {
            return itens.filter(function (item) { return !item.hidden; });
        }

        function destacar(posicao) {
            var lista = visiveis();
            itens.forEach(function (item) { item.classList.remove('sm-ativa'); });
            if (!lista.length) {
                ativa = -1;
                busca.removeAttribute('aria-activedescendant');
                return;
            }
            ativa = Math.max(0, Math.min(posicao, lista.length - 1));
            var alvo = lista[ativa];
            alvo.classList.add('sm-ativa');
            busca.setAttribute('aria-activedescendant', alvo.id);
            alvo.scrollIntoView({block: 'nearest'});
        }

        function filtrar() {
            var termos = normalizar(busca.value).split(/\s+/).filter(Boolean);
            var algum = false;
            opcoes.forEach(function (opcao, i) {
                var combina = termos.every(function (t) { return opcao.busca.indexOf(t) !== -1; });
                itens[i].hidden = !combina;
                if (combina) algum = true;
            });
            vazio.hidden = algum;
            destacar(0);
        }

        function abrir() {
            if (!painel.hidden) return;
            painel.hidden = false;
            raiz.classList.add('sm-aberto');
            busca.setAttribute('aria-expanded', 'true');
            filtrar();
        }

        function fechar() {
            painel.hidden = true;
            raiz.classList.remove('sm-aberto');
            busca.setAttribute('aria-expanded', 'false');
            busca.removeAttribute('aria-activedescendant');
        }

        function alternar(i) {
            var cb = opcoes[i].checkbox;
            cb.checked = !cb.checked;
            cb.dispatchEvent(new Event('change', {bubbles: true}));
            atualizar();
        }

        function atualizar() {
            etiquetas.innerHTML = '';
            var total = 0;
            opcoes.forEach(function (opcao, i) {
                var marcado = opcao.checkbox.checked;
                itens[i].setAttribute('aria-selected', marcado ? 'true' : 'false');
                itens[i].classList.toggle('sm-selecionada', marcado);
                if (!marcado) return;
                total++;
                var etiqueta = criar('span', 'sm-etiqueta');
                etiqueta.appendChild(criar('span', 'sm-etiqueta-texto', opcao.nome));
                var remover = criar('button', 'sm-remover', '×');
                remover.type = 'button';
                remover.setAttribute('aria-label', 'Remover ' + opcao.nome);
                remover.addEventListener('click', function (e) {
                    e.stopPropagation();
                    alternar(i);
                    busca.focus();
                });
                etiqueta.appendChild(remover);
                etiquetas.appendChild(etiqueta);
            });
            contador.textContent = total === 0 ? 'Nenhum professor selecionado'
                : total === 1 ? '1 professor selecionado' : total + ' professores selecionados';
        }

        campo.addEventListener('mousedown', function (e) {
            if (e.target.closest('.sm-remover')) return;
            if (e.target !== busca) e.preventDefault();
            busca.focus();
            abrir();
        });
        busca.addEventListener('focus', abrir);
        busca.addEventListener('input', function () { abrir(); filtrar(); });
        busca.addEventListener('blur', function () { setTimeout(fechar, 0); });
        busca.addEventListener('keydown', function (e) {
            if (e.key === 'ArrowDown') { e.preventDefault(); abrir(); destacar(ativa + 1); }
            else if (e.key === 'ArrowUp') { e.preventDefault(); abrir(); destacar(ativa - 1); }
            else if (e.key === 'Enter') {
                // Enter escolhe a opção destacada em vez de enviar o formulário
                e.preventDefault();
                var alvo = visiveis()[ativa];
                if (!painel.hidden && alvo) alternar(itens.indexOf(alvo));
            } else if (e.key === 'Escape') { fechar(); }
            else if (e.key === 'Backspace' && busca.value === '') {
                var marcados = opcoes.map(function (o, i) { return o.checkbox.checked ? i : -1; })
                    .filter(function (i) { return i >= 0; });
                if (marcados.length) alternar(marcados[marcados.length - 1]);
            }
        });

        atualizar();
    }

    document.addEventListener('DOMContentLoaded', function () {
        Array.prototype.forEach.call(document.querySelectorAll('[data-seletor-multiplo]'), montar);
    });
})();
