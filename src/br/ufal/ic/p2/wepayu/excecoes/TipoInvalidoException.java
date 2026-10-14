package br.ufal.ic.p2.wepayu.excecoes;

public class TipoInvalidoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public TipoInvalidoException() {
        super("Tipo invalido.");
    }
}
