package br.ufal.ic.p2.wepayu.excecoes;

public class IdentificacaoSindicatoNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public IdentificacaoSindicatoNulaException() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}
