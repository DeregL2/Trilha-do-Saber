package com.tcc.trilha_do_saber.config;

import com.tcc.trilha_do_saber.dto.UsuarioSessaoDTO;
import com.tcc.trilha_do_saber.service.ProvaDoAlunoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

public class ProvaEmAndamentoInterceptor implements HandlerInterceptor {

    private final ProvaDoAlunoService provaDoAlunoService;

    public ProvaEmAndamentoInterceptor(ProvaDoAlunoService provaDoAlunoService) {
        this.provaDoAlunoService = provaDoAlunoService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        if (session == null) {
            return true;
        }

        Object logado = session.getAttribute("usuarioLogado");
        if (!(logado instanceof UsuarioSessaoDTO aluno) || !"ALUNO".equals(aluno.getTipo())) {
            return true;
        }

        String caminho = caminho(request);
        if (liberado(caminho)) {
            return true;
        }

        Optional<Long> simuladoId = provaDoAlunoService.simuladoAplicadoEmAndamento(aluno.getId());
        if (simuladoId.isEmpty() || caminho.startsWith("/simulado/" + simuladoId.get() + "/")) {
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/simulado/" + simuladoId.get() + "/questao/1");
        return false;
    }

    static boolean liberado(String caminho) {
        return caminho.equals("/logout")
                || caminho.equals("/error")
                || caminho.equals("/favicon.ico")
                || caminho.startsWith("/css/")
                || caminho.startsWith("/js/")
                || caminho.startsWith("/img/")
                || caminho.startsWith("/images/")
                || caminho.matches("/questao/\\d+/imagem");
    }

    private String caminho(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contexto = request.getContextPath();
        if (contexto != null && !contexto.isEmpty() && uri.startsWith(contexto)) {
            return uri.substring(contexto.length());
        }
        return uri;
    }
}