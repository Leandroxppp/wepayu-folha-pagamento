package br.ufal.ic.p2.wepayu.excecoes;

public class ValorNaoBooleano extends Exception {
    public ValorNaoBooleano() {
        super("Valor deve ser true ou false.");
    }
}
