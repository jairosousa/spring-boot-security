package com.mballem.curso.security.web.controller;

import com.mballem.curso.security.domain.Agendamento;
import com.mballem.curso.security.domain.Especialidade;
import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.service.AgendamentoService;
import com.mballem.curso.security.service.EspecialidadeService;
import com.mballem.curso.security.service.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Controller
@RequestMapping("agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService service;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private EspecialidadeService especialidadeService;

    @GetMapping("agendar")
    public String agendarConsulta(Agendamento agendamento) {

        return "agendamento/cadastro";
    }

    /**
     * Busca os horarios livres ou seja sem agendamento
     *
     * @param id
     * @param data
     * @return
     */
    @GetMapping("horario/medico/{id}/data/{data}")
    public ResponseEntity<?> getHorarios(@PathVariable("id") Long id,
                                         @PathVariable("data") @DateTimeFormat(iso = ISO.DATE) LocalDate data) {

        return ResponseEntity.ok(service.buscarHorariosNaoAgendadosPorData(id, data));
    }

    /**
     * salva uma consulta agendada
     *
     * @param agendamento
     * @param attr
     * @param user
     * @return
     */
    @PostMapping("salvar")
    public String salvar(Agendamento agendamento,
                         RedirectAttributes attr,
                         @AuthenticationPrincipal User user) {
        Paciente paciente = pacienteService.buscarPorUsuarioEmail(user.getUsername());
        String titulo = agendamento.getEspecialidade().getTitulo();
        Especialidade especialidade = especialidadeService.buscaPorTitulos(
                new String[]{titulo}
        ).stream().findFirst().get();
        agendamento.setEspecialidade(especialidade);
        agendamento.setPaciente(paciente);
        service.salvar(agendamento);
        attr.addFlashAttribute("sucesso", "Sua consulta foi agendada com sucesso.");
        return "redirect:/agendamentos/agendar";
    }

    /**
     * Abre a pagina de historico de agendamento do paciente
     *
     * @return
     */
    @GetMapping({"historico/paciente", "historico/consultas"})
    public String historico() {
        return "agendamento/historico-paciente";
    }

    /**
     * Localizar o historico de agendamento por usuario.
     *
     * @param request
     * @param user
     * @return
     */
    @GetMapping("datatables/server/historico")
    public ResponseEntity<?> historicoAgendamentosPorPacientes(HttpServletRequest request, @AuthenticationPrincipal User user) {
        if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.PACIENTE.getDesc()))) {

            return ResponseEntity.ok(service.buscaHistoricoPorPacienteEmail(user.getUsername(), request));
        }

        if (user.getAuthorities().contains(new SimpleGrantedAuthority(PerfilTipo.MEDICO.getDesc()))) {


            return ResponseEntity.ok(service.buscaHistoricoPorMedicoEmail(user.getUsername(), request));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("editar/consulta/{id}")
    public String preEditarConsultaPaciente(@PathVariable("id") Long id,
                                            ModelMap model, @AuthenticationPrincipal User user) {
        Agendamento agendamento = service.buscarPorIdUsuario(id, user.getUsername());

        model.addAttribute("agendamento", agendamento);
        return "agendamento/cadastro";
    }


    @PostMapping("editar")
    public String editarConsulta(Agendamento agendamento,
                         RedirectAttributes attr,
                         @AuthenticationPrincipal User user) {

        String titulo = agendamento.getEspecialidade().getTitulo();
        Especialidade especialidade = especialidadeService.buscaPorTitulos(
                new String[]{titulo}
        ).stream().findFirst().get();
        agendamento.setEspecialidade(especialidade);

        service.editar(agendamento, user.getUsername());
        attr.addFlashAttribute("sucesso", "Sua consulta foi altarada com sucesso.");
        return "redirect:/agendamentos/agendar";
    }
}
