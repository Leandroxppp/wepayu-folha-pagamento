package br.ufal.ic.p2.wepayu.excecoes;

public class HorasNulaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public HorasNulaException() {
        super("Horas nao pode ser nulo.");
    }
}
