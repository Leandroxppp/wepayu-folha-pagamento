package br.ufal.ic.p2.wepayu.excecoes;

public class AgenciaNula extends Exception {
    public AgenciaNula() {
        super("Agencia nao pode ser nulo.");
    }
}
