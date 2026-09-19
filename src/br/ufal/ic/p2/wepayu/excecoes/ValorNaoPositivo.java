package br.ufal.ic.p2.wepayu.excecoes;

public class ValorNaoPositivo extends Exception {
    public ValorNaoPositivo() {
        super("Valor deve ser positivo.");
    }
}
