package br.ufal.ic.p2.wepayu.excecoes;

public class EmpregadoNaoEncontradoPorNome extends Exception {
    public EmpregadoNaoEncontradoPorNome() {
        super("Nao ha empregado com esse nome.");
    }
}
