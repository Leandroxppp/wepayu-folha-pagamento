package br.ufal.ic.p2.wepayu.excecoes;

public class ComissaoNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ComissaoNulaException() {
        super("Comissao nao pode ser nula.");
    }
}
