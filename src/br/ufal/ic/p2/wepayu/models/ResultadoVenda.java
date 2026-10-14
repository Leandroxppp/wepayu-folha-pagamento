package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.excecoes.ValorNaoPositivoException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Venda realizada por um empregado comissionado. */
public class ResultadoVenda implements Lancamento, Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDate data;
    private final BigDecimal valor;

    public ResultadoVenda(LocalDate data, BigDecimal valor) throws ValorNaoPositivoException {
        if (valor == null || valor.signum() <= 0) {
            throw new ValorNaoPositivoException();
        }
        this.data = data;
        this.valor = valor;
    }

    @Override
    public LocalDate getData() {
        return data;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
