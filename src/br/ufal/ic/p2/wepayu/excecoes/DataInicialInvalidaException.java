package br.ufal.ic.p2.wepayu.excecoes;

public class DataInicialInvalidaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public DataInicialInvalidaException() {
        super("Data inicial invalida.");
    }
}
