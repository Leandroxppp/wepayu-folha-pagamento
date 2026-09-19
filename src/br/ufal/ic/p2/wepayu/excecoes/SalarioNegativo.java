package br.ufal.ic.p2.wepayu.excecoes;

public class SalarioNegativo extends Exception {
    public SalarioNegativo() {
        super("Salario deve ser nao-negativo.");
    }
}
