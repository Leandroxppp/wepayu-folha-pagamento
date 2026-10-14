package br.ufal.ic.p2.wepayu.excecoes;

public class SemComandoRefazerException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public SemComandoRefazerException() {
        super("Nao ha comando a refazer.");
    }
}
