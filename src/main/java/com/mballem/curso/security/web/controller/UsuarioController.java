
package com.mballem.curso.security.web.controller;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("u")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private MedicoService medicoService;

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

        } else if (us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
            Medico medico = medicoService.buscarPorUsuarioId(usuarioId);

            return medico.hasNotId()
                    ? new ModelAndView("medico/cadastro", "medico", new Medico(new Usuario(usuarioId)))
                    : new ModelAndView("medico/cadastro", "medico", medico);

        } else if (us.getPerfis().contains(new Perfil(PerfilTipo.PACIENTE.getCod()))) {
            ModelAndView model = new ModelAndView("error");
            model.addObject("status", 403);
            model.addObject("error", "Área Restrita");
            model.addObject("message", "Os dados de paciente são restrito a ele");
            return model;
        }

        return new ModelAndView("redirect:/u/lista");
    }

    @GetMapping("editar/senha")
    public String abrirEditarSenha() {
        return "usuario/editar-senha";
    }

    @PostMapping("confirmar/senha")
    public String editarSenha(@RequestParam("senha1") String s1,
                              @RequestParam("senha2") String s2,
                              @RequestParam("senha3") String s3,
                              @AuthenticationPrincipal User user,
                              RedirectAttributes attr) {
        if (!s1.equals(s2)) {
            attr.addFlashAttribute("falha", "Senhas não conferem, tente novamente");
            return "redirect:/u/editar/senha";
        }

        Usuario u = service.buscarPorEmail(user.getUsername());
        if (!UsuarioService.isSenhaCorreta(s3, u.getSenha())) {
            attr.addFlashAttribute("falha", "Senhas atual não conferem, tente novamente");
            return "redirect:/u/editar/senha";
        }

        service.alterarSenha(u, s1);
        attr.addFlashAttribute("sucesso", "Senhas alterada com sucesso");
        return "redirect:/u/editar/senha";
    }

    /**
     * Abre a pagina de cadastro de usuario
     *
     * @param usuario
     * @return
     */
    @GetMapping("novo/cadastro")
    public String novoCadastro(Usuario usuario) {
        return "cadastrar-se";
    }

    @GetMapping("cadastro/realizado")
    public String cadastroRealizado() {
        return "fragments/mensagem";
    }

    @PostMapping("cadastro/paciente/salvar")
    public String salvarCadastroPaciente(Usuario usuario, BindingResult result) {
        try {
            service.salvarCadastroPaciente(usuario);
        }
        // Dispara quando usuario tenta cadastrar usuario já cadastrado
        catch (DataIntegrityViolationException ex) {
            result.reject("email", "Ops... este e-mail já existe na base de dados");
            return "cadastrar-se";
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return "redirect:/u/cadastro/realizado";
    }


}
