package br.ufal.ic.p2.wepayu.excecoes;

public class TaxaSindicalNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public TaxaSindicalNulaException() {
        super("Taxa sindical nao pode ser nula.");
    }
}
