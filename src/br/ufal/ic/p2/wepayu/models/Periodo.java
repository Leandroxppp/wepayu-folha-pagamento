package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/** Intervalo de datas, com início inclusivo e fim exclusivo. */
public final class Periodo {

    private final LocalDate inicio;
    private final LocalDate fim;

    private Periodo(LocalDate inicio, LocalDate fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    /** Período [inicio, fim): inclui a data inicial e não inclui a final (consultas do usuário). */
    public static Periodo entre(LocalDate inicio, LocalDate fim) {
        return new Periodo(inicio, fim);
    }

    /** Período (ultimoDia, ate]: começa no dia seguinte a {@code ultimoDia} e inclui {@code ate} (folha). */
    public static Periodo apos(LocalDate ultimoDia, LocalDate ate) {
        return new Periodo(ultimoDia.plusDays(1), ate.plusDays(1));
    }

    public boolean contem(LocalDate data) {
        return !data.isBefore(inicio) && data.isBefore(fim);
    }

    public <T extends Lancamento> List<T> filtrar(List<T> lancamentos) {
        return lancamentos.stream().filter(l -> contem(l.getData())).collect(Collectors.toList());
    }
}
