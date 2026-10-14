package br.ufal.ic.p2.wepayu.excecoes;

public class TipoNaoAplicavelException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public TipoNaoAplicavelException() {
        super("Tipo nao aplicavel.");
    }
}
