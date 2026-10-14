package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoHoristaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EmpregadoNaoHoristaException() {
        super("Empregado nao eh horista.");
    }
}
