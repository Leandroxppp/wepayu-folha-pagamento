package br.ufal.ic.p2.wepayu.excecoes;

public class BancoNuloException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public BancoNuloException() {
        super("Banco nao pode ser nulo.");
    }
}
