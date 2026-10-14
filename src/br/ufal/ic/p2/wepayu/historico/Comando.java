package br.ufal.ic.p2.wepayu.historico;

import br.ufal.ic.p2.wepayu.excecoes.WePayUException;

/** Operação que altera o sistema e devolve um resultado. */
@FunctionalInterface
public interface Comando<T> {

    T executar() throws WePayUException;
}
