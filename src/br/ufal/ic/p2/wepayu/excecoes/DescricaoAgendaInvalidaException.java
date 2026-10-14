package br.ufal.ic.p2.wepayu.excecoes;

public class DescricaoAgendaInvalidaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public DescricaoAgendaInvalidaException() {
        super("Descricao de agenda invalida");
    }
}
