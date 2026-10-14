package br.ufal.ic.p2.wepayu.excecoes;

public class ValorNaoPositivoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ValorNaoPositivoException() {
        super("Valor deve ser positivo.");
    }
}
