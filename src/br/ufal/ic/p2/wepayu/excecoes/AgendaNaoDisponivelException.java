package br.ufal.ic.p2.wepayu.excecoes;

public class AgendaNaoDisponivelException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public AgendaNaoDisponivelException() {
        super("Agenda de pagamento nao esta disponivel");
    }
}
