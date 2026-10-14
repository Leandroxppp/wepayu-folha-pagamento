package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoExisteException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoExisteException() {
        super("Empregado nao existe.");
    }
}
