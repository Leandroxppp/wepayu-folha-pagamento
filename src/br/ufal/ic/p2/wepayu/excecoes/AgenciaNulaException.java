package br.ufal.ic.p2.wepayu.excecoes;

public class AgenciaNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public AgenciaNulaException() {
        super("Agencia nao pode ser nulo.");
    }
}
