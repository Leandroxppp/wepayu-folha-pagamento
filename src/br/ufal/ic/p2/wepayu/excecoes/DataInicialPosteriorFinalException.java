package br.ufal.ic.p2.wepayu.excecoes;

public class DataInicialPosteriorFinalException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public DataInicialPosteriorFinalException() {
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
