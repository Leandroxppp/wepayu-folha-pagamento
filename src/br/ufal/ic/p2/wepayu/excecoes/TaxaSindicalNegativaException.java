package br.ufal.ic.p2.wepayu.excecoes;

public class TaxaSindicalNegativaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public TaxaSindicalNegativaException() {
        super("Taxa sindical deve ser nao-negativa.");
    }
}
