package br.ufal.ic.p2.wepayu.excecoes;

public class HorasNaoNumerica extends Exception {
    public HorasNaoNumerica() {
        super("Horas deve ser numerico.");
    }
}
