package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoEncontradoPorNomeException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoEncontradoPorNomeException() {
        super("Nao ha empregado com esse nome.");
    }
}
