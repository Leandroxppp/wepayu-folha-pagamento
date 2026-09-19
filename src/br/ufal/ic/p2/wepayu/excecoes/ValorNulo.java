package br.ufal.ic.p2.wepayu.excecoes;

public class ValorNulo extends Exception {
    public ValorNulo() {
        super("Valor nao pode ser nulo.");
    }
}
