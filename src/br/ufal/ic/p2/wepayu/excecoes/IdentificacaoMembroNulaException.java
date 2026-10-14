package br.ufal.ic.p2.wepayu.excecoes;

public class IdentificacaoMembroNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public IdentificacaoMembroNulaException() {
        super("Identificacao do membro nao pode ser nula.");
    }
}
