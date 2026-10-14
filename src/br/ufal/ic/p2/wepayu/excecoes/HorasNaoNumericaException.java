package br.ufal.ic.p2.wepayu.excecoes;

public class HorasNaoNumericaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public HorasNaoNumericaException() {
        super("Horas deve ser numerico.");
    }
}
