package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Registro imutável de horas trabalhadas em um dia. */
public final class CartaoPonto implements Serializable, Lancamento {
    private static final long serialVersionUID = 1L;
    private final LocalDate data;
    private final BigDecimal horas;

    public CartaoPonto(LocalDate data, BigDecimal horas) {
        this.data = data;
        this.horas = horas;
    }
    @Override public LocalDate getData() { return data; }
    public BigDecimal getHoras() { return horas; }
}
