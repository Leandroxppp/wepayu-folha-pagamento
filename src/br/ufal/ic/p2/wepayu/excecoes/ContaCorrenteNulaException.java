package br.ufal.ic.p2.wepayu.excecoes;

public class ContaCorrenteNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ContaCorrenteNulaException() {
        super("Conta corrente nao pode ser nulo.");
    }
}
