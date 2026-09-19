package br.ufal.ic.p2.wepayu.excecoes;

public class ValorNaoNumerico extends Exception {
    public ValorNaoNumerico() {
        super("Valor deve ser numerico.");
    }
}
