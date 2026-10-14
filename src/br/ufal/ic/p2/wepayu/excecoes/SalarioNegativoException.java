package br.ufal.ic.p2.wepayu.excecoes;

public class SalarioNegativoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public SalarioNegativoException() {
        super("Salario deve ser nao-negativo.");
    }
}
