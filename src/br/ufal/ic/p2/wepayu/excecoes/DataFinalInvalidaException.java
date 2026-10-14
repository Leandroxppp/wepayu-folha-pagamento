package br.ufal.ic.p2.wepayu.excecoes;

public class DataFinalInvalidaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public DataFinalInvalidaException() {
        super("Data final invalida.");
    }
}
