package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoSindicalizado extends Exception {
    public EmpregadoNaoSindicalizado() {
        super("Empregado nao eh sindicalizado.");
    }
}
