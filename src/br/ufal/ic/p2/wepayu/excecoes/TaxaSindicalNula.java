package br.ufal.ic.p2.wepayu.excecoes;

public class TaxaSindicalNula extends Exception {
    public TaxaSindicalNula() {
        super("Taxa sindical nao pode ser nula.");
    }
}
