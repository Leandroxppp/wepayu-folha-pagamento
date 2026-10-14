package br.ufal.ic.p2.wepayu.excecoes;

public class IdSindicatoDuplicadoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public IdSindicatoDuplicadoException() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
