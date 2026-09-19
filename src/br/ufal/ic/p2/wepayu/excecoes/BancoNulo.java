package br.ufal.ic.p2.wepayu.excecoes;

public class BancoNulo extends Exception {
    public BancoNulo() {
        super("Banco nao pode ser nulo.");
    }
}
