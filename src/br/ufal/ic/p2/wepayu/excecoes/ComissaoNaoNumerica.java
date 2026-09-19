package br.ufal.ic.p2.wepayu.excecoes;

public class ComissaoNaoNumerica extends Exception {
    public ComissaoNaoNumerica() {
        super("Comissao deve ser numerica.");
    }
}
