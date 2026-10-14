package br.ufal.ic.p2.wepayu.agenda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

/** Agenda "mensal D" (dia 1 a 28 do mês) ou "mensal $" (último dia útil do mês). */
public class AgendaMensal extends AgendaPagamento {
    private static final long serialVersionUID = 1L;

    public static final int ULTIMO_DIA_UTIL = 0;

    private final int diaDoMes;

    public AgendaMensal(int diaDoMes, String descricao) {
        super(descricao);
        this.diaDoMes = diaDoMes;
    }

    @Override
    public boolean ehDiaDePagamento(LocalDate data) {
        return data.equals(diaDePagamentoNoMes(YearMonth.from(data)));
    }

    @Override
    public LocalDate pagamentoAnterior(LocalDate data) {
        return diaDePagamentoNoMes(YearMonth.from(data).minusMonths(1));
    }

    @Override
    public BigDecimal calcularSalarioFixo(BigDecimal salarioMensal) {
        return salarioMensal.setScale(2, RoundingMode.FLOOR);
    }

    private LocalDate diaDePagamentoNoMes(YearMonth mes) {
        return diaDoMes == ULTIMO_DIA_UTIL ? CalendarioFolha.ultimoDiaUtil(mes) : mes.atDay(diaDoMes);
    }
}
