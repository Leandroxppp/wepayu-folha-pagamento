package br.ufal.ic.p2.wepayu.excecoes;

public class HorasNaoPositivas extends Exception {
    public HorasNaoPositivas() {
        super("Horas devem ser positivas.");
    }
}
