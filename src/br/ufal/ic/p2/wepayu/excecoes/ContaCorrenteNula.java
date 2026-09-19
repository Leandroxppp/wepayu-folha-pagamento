package br.ufal.ic.p2.wepayu.excecoes;

public class ContaCorrenteNula extends Exception {
    public ContaCorrenteNula() {
        super("Conta corrente nao pode ser nulo.");
    }
}
