package com.mballem.curso.security.service;

import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository repository;

    @Transactional(readOnly = true)
    public Paciente buscarPorUsuarioEmail(String email) {
        return repository.findByUsuarioEmail(email).orElse(new Paciente());
    }

    @Transactional(readOnly = false)
    public void salvar(Paciente paciente) {
        repository.save(paciente);
    }

    @Transactional(readOnly = false)
    public void editar(Paciente paciente) {
        // Trabalhando com objeto persistente
        Paciente p2 = repository.findById(paciente.getId()).get();
        p2.setNome(paciente.getNome());
        p2.setDtNascimento(paciente.getDtNascimento());
    }
}
