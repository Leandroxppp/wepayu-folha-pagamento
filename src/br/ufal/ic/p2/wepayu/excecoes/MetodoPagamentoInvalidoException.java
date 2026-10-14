package br.ufal.ic.p2.wepayu.excecoes;

public class MetodoPagamentoInvalidoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public MetodoPagamentoInvalidoException() {
        super("Metodo de pagamento invalido.");
    }
}
