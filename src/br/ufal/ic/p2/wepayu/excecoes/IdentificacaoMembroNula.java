package br.ufal.ic.p2.wepayu.excecoes;

public class IdentificacaoMembroNula extends Exception {
    public IdentificacaoMembroNula() {
        super("Identificacao do membro nao pode ser nula.");
    }
}
