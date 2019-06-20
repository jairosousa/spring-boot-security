package com.mballem.curso.security.web.controller;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("u")
public class UsuarioController {

    @Autowired
    private UsuarioService service;
    //    Abrir cadastro de usuários (Médico/admin/paciente)
    @GetMapping("novo/cadastro/usuario")
    public String cadastroPorAdminParaAdminMedicoPaciente(Usuario usuario) {
        return "usuario/cadastro";
    }

    @GetMapping("lista")
    public String listaUsuarios(){
        return "usuario/lista";
    }

    @GetMapping("datatables/server/usuarios")
    public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request){
        return ResponseEntity.ok(service.buscarTodos(request));
    }
}
