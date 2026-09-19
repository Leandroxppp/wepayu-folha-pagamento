package br.ufal.ic.p2.wepayu.excecoes;

public class ComissaoNegativa extends Exception {
    public ComissaoNegativa() {
        super("Comissao deve ser nao-negativa.");
    }
}
