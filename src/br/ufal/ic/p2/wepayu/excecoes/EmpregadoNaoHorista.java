package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoHorista extends Exception {
    public EmpregadoNaoHorista() {
        super("Empregado nao eh horista.");
    }
}
