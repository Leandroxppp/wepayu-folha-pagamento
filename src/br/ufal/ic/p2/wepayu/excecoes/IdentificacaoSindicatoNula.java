package br.ufal.ic.p2.wepayu.excecoes;

public class IdentificacaoSindicatoNula extends Exception {
    public IdentificacaoSindicatoNula() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}
