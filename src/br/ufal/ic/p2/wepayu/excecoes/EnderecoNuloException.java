package br.ufal.ic.p2.wepayu.excecoes;

public class EnderecoNuloException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public EnderecoNuloException() {
        super("Endereco nao pode ser nulo.");
    }
}
