package br.ufal.ic.p2.wepayu.excecoes;

public class TaxaSindicalNaoNumericaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public TaxaSindicalNaoNumericaException() {
        super("Taxa sindical deve ser numerica.");
    }
}
