package br.ufal.ic.p2.wepayu.excecoes;

public class SalarioNaoNumericoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public SalarioNaoNumericoException() {
        super("Salario deve ser numerico.");
    }
}
