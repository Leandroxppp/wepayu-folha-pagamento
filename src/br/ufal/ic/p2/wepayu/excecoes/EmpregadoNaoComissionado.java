package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoComissionado extends Exception {
    public EmpregadoNaoComissionado() {
        super("Empregado nao eh comissionado.");
    }
}
