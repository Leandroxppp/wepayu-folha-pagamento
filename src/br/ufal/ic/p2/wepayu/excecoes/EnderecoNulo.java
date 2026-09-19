package br.ufal.ic.p2.wepayu.excecoes;

public class EnderecoNulo extends Exception {
    public EnderecoNulo() {
        super("Endereco nao pode ser nulo.");
    }
}
