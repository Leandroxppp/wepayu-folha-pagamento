package br.ufal.ic.p2.wepayu.excecoes;

public class HorasNula extends Exception {
    public HorasNula() {
        super("Horas nao pode ser nulo.");
    }
}
