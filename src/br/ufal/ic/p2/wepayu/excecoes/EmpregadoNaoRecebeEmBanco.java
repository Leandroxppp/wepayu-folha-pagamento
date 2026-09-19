package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoRecebeEmBanco extends Exception {
    public EmpregadoNaoRecebeEmBanco() {
        super("Empregado nao recebe em banco.");
    }
}
