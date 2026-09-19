package br.ufal.ic.p2.wepayu.excecoes;

public class DataInicialPosteriorFinal extends Exception {
    public DataInicialPosteriorFinal() {
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
