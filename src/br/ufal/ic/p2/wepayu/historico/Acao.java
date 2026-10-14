package br.ufal.ic.p2.wepayu.historico;

import br.ufal.ic.p2.wepayu.excecoes.WePayUException;

/** Operação que altera o sistema e não devolve nada. */
@FunctionalInterface
public interface Acao {

    void executar() throws WePayUException;
}
