package com.mballem.curso.security.web.controller;

import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
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

    // Pre Editar usuário de credenciais
    @GetMapping("editar/credenciais/usuario/{id}")
    public ModelAndView preEditarCredenciais(@PathVariable("id") Long id) {
        return new ModelAndView("usuario/cadastro", "usuario", service.buscarPorId(id));
    }

    // Pre Editar de cadastros de usuário
    @GetMapping("editar/dados/usuario/{id}/perfis/{perfis}")
    public ModelAndView preEditarCadrastroDadosPessoais(@PathVariable("id") Long usuarioId,
                                                        @PathVariable("perfis") Long[] perfisId) {
        Usuario us = service.buscarPorIdEPerfis(usuarioId, perfisId);

        if (us.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod())) &&
                !us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {

            return new ModelAndView("usuario/cadastro", "usuario", us);
        } else if (us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))){

            return new ModelAndView("especialidade/especialidade");
        } else if(us.getPerfis().contains(new Perfil(PerfilTipo.PACIENTE.getCod()))) {
            ModelAndView model = new ModelAndView("error");
            model.addObject("status", 403);
            model.addObject("error", "Área Restrita");
            model.addObject("message", "Os dados de paciente são restrito a ele");
            return model;
        }

        return new ModelAndView("redirect:/u/lista");
    }
}
