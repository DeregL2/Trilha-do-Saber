(function () {
    var OLHO = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" '
        + 'stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">'
        + '<path d="M1 12s4-7 11-7 11 7 11 7-4 7-11 7S1 12 1 12z"/><circle cx="12" cy="12" r="3"/></svg>';

    var OLHO_RISCADO = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" '
        + 'stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">'
        + '<path d="M17.94 17.94A10.07 10.07 0 0 1 12 19c-7 0-11-7-11-7a18.45 18.45 0 0 1 5.06-5.94"/>'
        + '<path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 7 11 7a18.5 18.5 0 0 1-2.16 3.19"/>'
        + '<path d="M14.12 14.12a3 3 0 1 1-4.24-4.24"/><line x1="1" y1="1" x2="23" y2="23"/></svg>';

    function preparar(campo) {
        if (campo.dataset.senhaPronta === "true") {
            return;
        }
        campo.dataset.senhaPronta = "true";
        campo.classList.add("campo-senha-entrada");

        var envoltorio = document.createElement("div");
        envoltorio.className = "campo-senha";
        campo.parentNode.insertBefore(envoltorio, campo);
        envoltorio.appendChild(campo);

        var botao = document.createElement("button");
        botao.type = "button";
        botao.className = "campo-senha-olho";
        envoltorio.appendChild(botao);

        function desenhar(mostrando) {
            botao.innerHTML = mostrando ? OLHO_RISCADO : OLHO;
            botao.setAttribute("aria-label", mostrando ? "Ocultar senha" : "Mostrar senha");
            botao.setAttribute("title", mostrando ? "Ocultar senha" : "Mostrar senha");
            botao.setAttribute("aria-pressed", mostrando ? "true" : "false");
        }

        function esconder() {
            campo.type = "password";
            desenhar(false);
        }

        botao.addEventListener("click", function () {
            var mostrando = campo.type === "password";
            campo.type = mostrando ? "text" : "password";
            desenhar(mostrando);
            campo.focus();
        });

        campo.addEventListener("senha:esconder", esconder);
        if (campo.form) {
            campo.form.addEventListener("submit", esconder);
        }
        desenhar(false);
    }

    function esconderTodos() {
        document.querySelectorAll("input.campo-senha-entrada").forEach(function (campo) {
            campo.dispatchEvent(new Event("senha:esconder"));
        });
    }

    document.querySelectorAll('input[type="password"]').forEach(preparar);

    document.querySelectorAll("dialog").forEach(function (dialogo) {
        dialogo.addEventListener("close", function () {
            dialogo.querySelectorAll("input.campo-senha-entrada").forEach(function (campo) {
                campo.dispatchEvent(new Event("senha:esconder"));
            });
        });
    });

    window.addEventListener("pagehide", esconderTodos);
})();