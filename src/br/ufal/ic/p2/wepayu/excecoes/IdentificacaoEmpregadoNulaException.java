package br.ufal.ic.p2.wepayu.excecoes;

public class IdentificacaoEmpregadoNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public IdentificacaoEmpregadoNulaException() {
        super("Identificacao do empregado nao pode ser nula.");
    }
}
