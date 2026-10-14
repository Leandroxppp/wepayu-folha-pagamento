package br.ufal.ic.p2.wepayu.excecoes;

public class SalarioNuloException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public SalarioNuloException() {
        super("Salario nao pode ser nulo.");
    }
}
