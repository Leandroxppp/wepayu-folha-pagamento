package br.ufal.ic.p2.wepayu.excecoes;

public class ValorNaoNumericoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ValorNaoNumericoException() {
        super("Valor deve ser numerico.");
    }
}
