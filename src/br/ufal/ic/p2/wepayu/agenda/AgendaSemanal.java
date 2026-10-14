package br.ufal.ic.p2.wepayu.agenda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/** Agenda "semanal [N] D": paga a cada N semanas (padrão 1) no dia D da semana (1 = segunda ... 7 = domingo). */
public class AgendaSemanal extends AgendaPagamento {
    private static final long serialVersionUID = 1L;

    private static final int SEMANAS_NO_ANO = 52;
    private static final int MESES_NO_ANO = 12;

    private final int intervaloEmSemanas;
    private final int diaDaSemana;

    public AgendaSemanal(int intervaloEmSemanas, int diaDaSemana, String descricao) {
        super(descricao);
        this.intervaloEmSemanas = intervaloEmSemanas;
        this.diaDaSemana = diaDaSemana;
    }

    @Override
    public boolean ehDiaDePagamento(LocalDate data) {
        if (data.getDayOfWeek().getValue() != diaDaSemana) {
            return false;
        }
        LocalDate primeiro = CalendarioFolha.DATA_INICIAL.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(diaDaSemana)));
        if (data.isBefore(primeiro)) {
            return false;
        }
        long semanasDesdeOPrimeiro = ChronoUnit.WEEKS.between(primeiro, data);
        return semanasDesdeOPrimeiro % intervaloEmSemanas == intervaloEmSemanas - 1;
    }

    @Override
    public LocalDate pagamentoAnterior(LocalDate data) {
        return data.minusWeeks(intervaloEmSemanas);
    }

    /** Salário anual (12 meses) dividido em 52 semanas, multiplicado pelas semanas do intervalo. */
    @Override
    public BigDecimal calcularSalarioFixo(BigDecimal salarioMensal) {
        return salarioMensal.multiply(BigDecimal.valueOf((long) MESES_NO_ANO * intervaloEmSemanas))
                .divide(BigDecimal.valueOf(SEMANAS_NO_ANO), 10, RoundingMode.FLOOR)
                .setScale(2, RoundingMode.FLOOR);
    }
}
