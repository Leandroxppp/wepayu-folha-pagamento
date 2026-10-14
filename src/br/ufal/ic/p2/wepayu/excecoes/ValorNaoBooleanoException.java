package br.ufal.ic.p2.wepayu.excecoes;

public class ValorNaoBooleanoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ValorNaoBooleanoException() {
        super("Valor deve ser true ou false.");
    }
}
