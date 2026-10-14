package br.ufal.ic.p2.wepayu.repositorio;

import java.io.Serializable;

/** Tudo o que o sistema guarda: é o objeto que vai para o arquivo e que o desfazer/refazer restaura. */
public class BaseDados implements Serializable {
    private static final long serialVersionUID = 1L;

    private final RepositorioEmpregados empregados = new RepositorioEmpregados();
    private final RepositorioAgendas agendas = new RepositorioAgendas();

    public RepositorioEmpregados getEmpregados() {
        return empregados;
    }

    public RepositorioAgendas getAgendas() {
        return agendas;
    }
}
