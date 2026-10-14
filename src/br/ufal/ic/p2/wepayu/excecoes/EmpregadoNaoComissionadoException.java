package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoComissionadoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoComissionadoException() {
        super("Empregado nao eh comissionado.");
    }
}
