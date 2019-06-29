package com.mballem.curso.security.service;

import com.mballem.curso.security.domain.Agendamento;
import com.mballem.curso.security.domain.Horario;
import com.mballem.curso.security.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository repository;

    @Transactional(readOnly = true)
    public List<Horario> buscarHorariosNaoAgendadosPorData(Long id, LocalDate data) {
        return repository.findByMedicoIdAndDataNotHorarioAgendado(id, data);
    }

    @Transactional(readOnly = false)
    public void salvar(Agendamento agendamento) {
        repository.save(agendamento);
    }

    @Transactional(readOnly = true)
    public Object buscaHistoricoPorMedicoEmail(String username, HttpServletRequest request) {
    }

    @Transactional(readOnly = true)
    public Object buscaHistoricoPorPacienteEmail(String username, HttpServletRequest request) {
    }
}
