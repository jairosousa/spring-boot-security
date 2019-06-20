package com.mballem.curso.security.web.controller;

import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

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
    public String listaUsuarios() {
        return "usuario/lista";
    }

    @GetMapping("datatables/server/usuarios")
    public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request) {
        return ResponseEntity.ok(service.buscarTodos(request));
    }

    /**
     * Salvar usuario por Administrador
     *
     * @param usuario
     * @param attr
     * @return
     */
    @PostMapping("cadastro/salvar")
    public String salvarUsuarios(Usuario usuario, RedirectAttributes attr) {
        List<Perfil> perfis = usuario.getPerfis();
        if (perfis.size() > 2
                || perfis.containsAll(Arrays.asList(new Perfil(1L), new Perfil(3L)))
                || perfis.containsAll(Arrays.asList(new Perfil(2L), new Perfil(3L)))) {
            attr.addFlashAttribute("falha", "Paciente Não pode ser admin e/ou médico");
            attr.addFlashAttribute("usuario", usuario);
        } else {
            try {
                service.salvarUsuario(usuario);
                attr.addFlashAttribute("sucesso", "Operação realizada com sucesso!");

            } catch (DataIntegrityViolationException e) {
                attr.addFlashAttribute("falha", "Falha! Cadastro não realizado, email já existente!");

            }
        }
        return "redirect:/u/novo/cadastro/usuario";
    }
}
