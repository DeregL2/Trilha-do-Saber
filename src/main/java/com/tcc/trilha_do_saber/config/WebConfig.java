package com.tcc.trilha_do_saber.config;

import com.tcc.trilha_do_saber.service.ProvaDoAlunoService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ProvaDoAlunoService provaDoAlunoService;

    public WebConfig(ProvaDoAlunoService provaDoAlunoService) {
        this.provaDoAlunoService = provaDoAlunoService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ProvaEmAndamentoInterceptor(provaDoAlunoService))
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/img/**", "/images/**", "/favicon.ico", "/error");
    }
}