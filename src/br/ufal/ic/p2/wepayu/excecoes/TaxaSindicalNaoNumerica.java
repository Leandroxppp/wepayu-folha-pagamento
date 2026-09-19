package br.ufal.ic.p2.wepayu.excecoes;

public class TaxaSindicalNaoNumerica extends Exception {
    public TaxaSindicalNaoNumerica() {
        super("Taxa sindical deve ser numerica.");
    }
}
