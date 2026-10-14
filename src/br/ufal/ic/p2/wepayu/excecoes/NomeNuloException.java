package br.ufal.ic.p2.wepayu.excecoes;

public class NomeNuloException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public NomeNuloException() {
        super("Nome nao pode ser nulo.");
    }
}
