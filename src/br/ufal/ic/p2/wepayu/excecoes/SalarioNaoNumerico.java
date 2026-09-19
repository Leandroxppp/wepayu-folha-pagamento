package br.ufal.ic.p2.wepayu.excecoes;

public class SalarioNaoNumerico extends Exception {
    public SalarioNaoNumerico() {
        super("Salario deve ser numerico.");
    }
}
