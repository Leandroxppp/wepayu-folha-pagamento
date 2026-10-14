package br.ufal.ic.p2.wepayu.servicos;

import br.ufal.ic.p2.wepayu.agenda.FabricaAgendas;
import br.ufal.ic.p2.wepayu.excecoes.AgendaJaExisteException;
import br.ufal.ic.p2.wepayu.excecoes.DescricaoAgendaInvalidaException;
import br.ufal.ic.p2.wepayu.repositorio.ContextoSistema;

/** Criação de novas agendas de pagamento, que passam a poder ser escolhidas pelos empregados. */
public class ServicoAgendas {

    private final ContextoSistema contexto;

    public ServicoAgendas(ContextoSistema contexto) {
        this.contexto = contexto;
    }

    public void criarAgendaDePagamentos(String descricao)
            throws DescricaoAgendaInvalidaException, AgendaJaExisteException {
        contexto.agendas().adicionar(FabricaAgendas.criar(descricao));
    }
}
