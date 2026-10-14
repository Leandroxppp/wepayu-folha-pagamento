package br.ufal.ic.p2.wepayu.excecoes;

public class ComissaoNaoNumericaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ComissaoNaoNumericaException() {
        super("Comissao deve ser numerica.");
    }
}
