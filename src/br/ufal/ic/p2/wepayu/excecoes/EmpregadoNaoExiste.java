package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoExiste extends Exception {
    public EmpregadoNaoExiste() {
        super("Empregado nao existe.");
    }
}
