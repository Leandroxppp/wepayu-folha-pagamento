package br.ufal.ic.p2.wepayu.excecoes;

public class DataInvalidaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public DataInvalidaException() {
        super("Data invalida.");
    }
}
