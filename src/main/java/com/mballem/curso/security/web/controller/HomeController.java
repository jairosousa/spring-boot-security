package com.mballem.curso.security.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class HomeController {

    // abrir pagina home
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    //	Abrir a pagina de login
    @GetMapping("login")
    public String login() {
        return "login";
    }

    //	Login invalido
    @GetMapping({"login-error"})
    public String loginError(ModelMap model) {
        model.addAttribute("alerta", "erro");
        model.addAttribute("titulo", "Credenciais inválidas");
        model.addAttribute("texto", "Login ou senha incorretos, tente novamente");
        model.addAttribute("subtexto", "Acesso permitido apenas para cadastros já ativado.");
		return "login";
    }

    //	Acesso Negado
    @GetMapping({"acesso-negado"})
    public String acessoNegado(ModelMap model, HttpServletResponse response) {
        model.addAttribute("status", response.getStatus());
        model.addAttribute("error", "Acesso Negado");
        model.addAttribute("message", "Você não tem permissão para essa area ou ação");
        return "error";
    }
}

