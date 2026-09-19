package br.ufal.ic.p2.wepayu.excecoes;

public class IdSindicatoDuplicado extends Exception {
    public IdSindicatoDuplicado() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
