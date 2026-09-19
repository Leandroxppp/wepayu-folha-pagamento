package br.ufal.ic.p2.wepayu.excecoes;

public class SemComandoDesfazer extends Exception {
    public SemComandoDesfazer() {
        super("Nao ha comando a desfazer.");
    }
}
