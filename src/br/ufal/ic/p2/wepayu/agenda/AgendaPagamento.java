package br.ufal.ic.p2.wepayu.agenda;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Agenda de pagamento: define em quais dias um empregado é pago e quanto do
 * salário mensal cabe em cada pagamento. É identificada pela sua descrição
 * (ex.: "semanal 5", "mensal $").
 */
public abstract class AgendaPagamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String descricao;

    protected AgendaPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Diz se a data é um dia de pagamento desta agenda. */
    public abstract boolean ehDiaDePagamento(LocalDate data);

    /** Data do pagamento imediatamente anterior ao pagamento de {@code data}. */
    public abstract LocalDate pagamentoAnterior(LocalDate data);

    /** Parte fixa do salário mensal paga em cada pagamento desta agenda (2 casas, truncado). */
    public abstract BigDecimal calcularSalarioFixo(BigDecimal salarioMensal);

    @Override
    public boolean equals(Object outro) {
        return outro instanceof AgendaPagamento && descricao.equals(((AgendaPagamento) outro).descricao);
    }

    @Override
    public int hashCode() {
        return descricao.hashCode();
    }

    @Override
    public String toString() {
        return descricao;
    }
}
