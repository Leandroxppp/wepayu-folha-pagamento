package br.ufal.ic.p2.wepayu.excecoes;

public class MembroNaoExisteException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public MembroNaoExisteException() {
        super("Membro nao existe.");
    }
}
