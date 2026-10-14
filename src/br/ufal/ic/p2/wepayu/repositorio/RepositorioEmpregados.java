package br.ufal.ic.p2.wepayu.repositorio;

import br.ufal.ic.p2.wepayu.excecoes.*;
import br.ufal.ic.p2.wepayu.models.Empregado;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Guarda os empregados da empresa e sabe procurá-los por identificação, nome e identificação sindical. */
public class RepositorioEmpregados implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Empregado> empregados = new LinkedHashMap<>();
    private int proximoId = 1;

    public String gerarId() {
        return String.valueOf(proximoId++);
    }

    public void adicionar(Empregado empregado) {
        empregados.put(empregado.getId(), empregado);
    }

    /** Troca o empregado de mesmo id (ex.: depois de mudar de tipo), mantendo a posição na lista. */
    public void substituir(Empregado empregado) {
        empregados.put(empregado.getId(), empregado);
    }

    public void remover(Empregado empregado) {
        empregados.remove(empregado.getId());
    }

    public int quantidade() {
        return empregados.size();
    }

    public List<Empregado> todos() {
        return new ArrayList<>(empregados.values());
    }

    public Empregado buscar(String id) throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {
        if (id == null || id.trim().isEmpty()) {
            throw new IdentificacaoEmpregadoNulaException();
        }
        Empregado empregado = empregados.get(id);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }
        return empregado;
    }

    /** Devolve o n-ésimo empregado (contando de 1) cujo nome contém o texto. */
    public Empregado buscarPorNome(String nome, int indice) throws EmpregadoNaoEncontradoPorNomeException {
        int encontrados = 0;
        for (Empregado empregado : empregados.values()) {
            if (empregado.getNome().contains(nome) && ++encontrados == indice) {
                return empregado;
            }
        }
        throw new EmpregadoNaoEncontradoPorNomeException();
    }

    public Empregado buscarPorIdSindicato(String idSindicato)
            throws IdentificacaoMembroNulaException, MembroNaoExisteException {
        if (idSindicato == null || idSindicato.trim().isEmpty()) {
            throw new IdentificacaoMembroNulaException();
        }
        for (Empregado empregado : empregados.values()) {
            if (empregado.isSindicalizado() && idSindicatoDe(empregado).equals(idSindicato)) {
                return empregado;
            }
        }
        throw new MembroNaoExisteException();
    }

    /** Diz se algum empregado, além de {@code excecao}, já usa esta identificação no sindicato. */
    public boolean idSindicatoEmUso(String idSindicato, Empregado excecao) {
        for (Empregado empregado : empregados.values()) {
            if (empregado != excecao && empregado.isSindicalizado() && idSindicatoDe(empregado).equals(idSindicato)) {
                return true;
            }
        }
        return false;
    }

    private static String idSindicatoDe(Empregado empregado) {
        try {
            return empregado.getSindicato().getId();
        } catch (EmpregadoNaoSindicalizadoException e) {
            throw new IllegalStateException("Empregado sindicalizado sem dados do sindicato", e);
        }
    }
}
