package br.ufal.ic.p2.wepayu.excecoes;

public class SemComandoRefazer extends Exception {
    public SemComandoRefazer() {
        super("Nao ha comando a refazer.");
    }
}
