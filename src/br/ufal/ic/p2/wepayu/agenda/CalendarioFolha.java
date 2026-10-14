package br.ufal.ic.p2.wepayu.agenda;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Referências de calendário usadas pelas agendas de pagamento.
 *
 * <p>O enunciado e os testes de aceitação contam as semanas a partir de
 * 1/1/2005: a primeira ocorrência de um dia da semana nessa data (ou depois
 * dela) é a "primeira semana" de uma agenda do tipo "semanal N D". Essa data é
 * a única "data mágica" do sistema e fica concentrada aqui.
 */
public final class CalendarioFolha {

    public static final LocalDate DATA_INICIAL = LocalDate.of(2005, 1, 1);

    private CalendarioFolha() {
    }

    /** Dia anterior ao início do calendário: ninguém foi pago até essa data. */
    public static LocalDate vesperaDoInicio() {
        return DATA_INICIAL.minusDays(1);
    }

    /** Último dia útil (segunda a sexta) do mês, sem considerar feriados. */
    public static LocalDate ultimoDiaUtil(YearMonth mes) {
        LocalDate dia = mes.atEndOfMonth();
        while (dia.getDayOfWeek() == DayOfWeek.SATURDAY || dia.getDayOfWeek() == DayOfWeek.SUNDAY) {
            dia = dia.minusDays(1);
        }
        return dia;
    }
}
