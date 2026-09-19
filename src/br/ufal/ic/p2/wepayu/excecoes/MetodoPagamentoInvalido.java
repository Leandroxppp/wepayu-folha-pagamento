package br.ufal.ic.p2.wepayu.excecoes;

public class MetodoPagamentoInvalido extends Exception {
    public MetodoPagamentoInvalido() {
        super("Metodo de pagamento invalido.");
    }
}
