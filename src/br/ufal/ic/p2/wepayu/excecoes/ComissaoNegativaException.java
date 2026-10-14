package br.ufal.ic.p2.wepayu.excecoes;

public class ComissaoNegativaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public ComissaoNegativaException() {
        super("Comissao deve ser nao-negativa.");
    }
}
