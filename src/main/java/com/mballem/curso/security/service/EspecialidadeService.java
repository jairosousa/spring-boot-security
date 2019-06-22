package com.mballem.curso.security.service;

import com.mballem.curso.security.datatables.Datatables;
import com.mballem.curso.security.datatables.DatatablesColunas;
import com.mballem.curso.security.domain.Especialidade;
import com.mballem.curso.security.repository.EspecialidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class EspecialidadeService {

    @Autowired
    private EspecialidadeRepository repository;

    @Autowired
    private Datatables datatables;

    @Transactional(readOnly = false)
    public void salvar(Especialidade especialidade) {
        repository.save(especialidade);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarEspecialidade(HttpServletRequest request) {
        datatables.setRequest(request);
        datatables.setColunas(DatatablesColunas.ESPECIALIDADES);
        Page<?> pages = datatables.getSearch().isEmpty()
                ? repository.findAll(datatables.getPageable())
                : repository.findAllByTitulo(datatables.getSearch(), datatables.getPageable());

        return datatables.getResponse(pages);
    }

    @Transactional(readOnly = false)
    public Especialidade buscaPorId(Long id) {
            return repository.findById(id).get();
    }

    @Transactional(readOnly = false)
    public void remover(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> buscarEspecialidadeByTermo(String termo) {
        return repository.findEspecialidadesByTermo(termo);
    }

    @Transactional(readOnly = true)
    public Set<Especialidade> buscaPorTitulos(String[] titulos) {
        return repository.findByTitulos(titulos);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarEspecialidadePorMedico(Long id, HttpServletRequest request) {
        datatables.setRequest(request);
        datatables.setColunas(DatatablesColunas.ESPECIALIDADES);
        Page<Especialidade> page = repository.findByIdMedico(id, datatables.getPageable());

        return datatables.getResponse(page);
    }
}
