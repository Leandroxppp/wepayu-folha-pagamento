package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Registro imutável de uma venda realizada. */
public final class ResultadoVenda implements Serializable, Lancamento {
    private static final long serialVersionUID = 1L;
    private final LocalDate data;
    private final BigDecimal valor;

    public ResultadoVenda(LocalDate data, BigDecimal valor) {
        this.data = data;
        this.valor = valor;
    }
    @Override public LocalDate getData() { return data; }
    public BigDecimal getValor() { return valor; }
}
