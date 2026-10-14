package br.ufal.ic.p2.wepayu.agenda;

import java.util.Arrays;
import java.util.List;

/** As agendas que existem por padrão no sistema e a agenda inicial de cada tipo de empregado. */
public final class AgendasPadrao {

    public static final AgendaPagamento SEMANAL_SEXTA = new AgendaSemanal(1, 5, "semanal 5");
    public static final AgendaPagamento MENSAL_ULTIMO_DIA_UTIL =
            new AgendaMensal(AgendaMensal.ULTIMO_DIA_UTIL, "mensal $");
    public static final AgendaPagamento QUINZENAL_SEXTA = new AgendaSemanal(2, 5, "semanal 2 5");

    private AgendasPadrao() {
    }

    public static List<AgendaPagamento> todas() {
        return Arrays.asList(SEMANAL_SEXTA, MENSAL_ULTIMO_DIA_UTIL, QUINZENAL_SEXTA);
    }
}
