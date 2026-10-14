package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoRecebeEmBancoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoRecebeEmBancoException() {
        super("Empregado nao recebe em banco.");
    }
}
