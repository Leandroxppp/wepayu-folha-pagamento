package br.ufal.ic.p2.wepayu.excecoes;

public class IdentificacaoEmpregadoNula extends Exception {
    public IdentificacaoEmpregadoNula() {
        super("Identificacao do empregado nao pode ser nula.");
    }
}
