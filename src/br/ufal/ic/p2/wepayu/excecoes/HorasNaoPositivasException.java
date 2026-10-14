package br.ufal.ic.p2.wepayu.excecoes;

public class HorasNaoPositivasException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public HorasNaoPositivasException() {
        super("Horas devem ser positivas.");
    }
}
