package br.ufal.ic.p2.wepayu.excecoes;

public class SalarioNulo extends Exception {
    public SalarioNulo() {
        super("Salario nao pode ser nulo.");
    }
}
