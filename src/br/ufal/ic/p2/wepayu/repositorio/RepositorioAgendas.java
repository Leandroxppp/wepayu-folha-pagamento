package br.ufal.ic.p2.wepayu.repositorio;

import br.ufal.ic.p2.wepayu.agenda.AgendaPagamento;
import br.ufal.ic.p2.wepayu.agenda.AgendasPadrao;
import br.ufal.ic.p2.wepayu.agenda.FabricaAgendas;
import br.ufal.ic.p2.wepayu.excecoes.AgendaJaExisteException;
import br.ufal.ic.p2.wepayu.excecoes.AgendaNaoDisponivelException;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/** Agendas de pagamento que os empregados podem escolher (as padrão mais as criadas pela direção). */
public class RepositorioAgendas implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, AgendaPagamento> agendas = new LinkedHashMap<>();

    public RepositorioAgendas() {
        for (AgendaPagamento agenda : AgendasPadrao.todas()) {
            agendas.put(agenda.getDescricao(), agenda);
        }
    }

    public void adicionar(AgendaPagamento agenda) throws AgendaJaExisteException {
        if (agendas.containsKey(agenda.getDescricao())) {
            throw new AgendaJaExisteException();
        }
        agendas.put(agenda.getDescricao(), agenda);
    }

    public AgendaPagamento buscar(String descricao) throws AgendaNaoDisponivelException {
        AgendaPagamento agenda = agendas.get(FabricaAgendas.normalizar(descricao));
        if (agenda == null) {
            throw new AgendaNaoDisponivelException();
        }
        return agenda;
    }
}
