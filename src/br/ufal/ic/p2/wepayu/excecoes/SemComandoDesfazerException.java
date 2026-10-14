package br.ufal.ic.p2.wepayu.excecoes;

public class SemComandoDesfazerException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public SemComandoDesfazerException() {
        super("Nao ha comando a desfazer.");
    }
}
