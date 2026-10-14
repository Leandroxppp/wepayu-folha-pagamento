package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Resultado do pagamento de um empregado numa data. Guarda uma "foto" do nome e
 * da forma de pagamento no momento em que foi emitido.
 */
public abstract class Contracheque implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDate data;
    private final String nomeEmpregado;
    private final String metodoPagamento;
    private final BigDecimal bruto;
    private BigDecimal descontos = BigDecimal.ZERO;

    protected Contracheque(LocalDate data, String nomeEmpregado, String metodoPagamento, BigDecimal bruto) {
        this.data = data;
        this.nomeEmpregado = nomeEmpregado;
        this.metodoPagamento = metodoPagamento;
        this.bruto = bruto;
    }

    public LocalDate getData() {
        return data;
    }

    public String getNomeEmpregado() {
        return nomeEmpregado;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public BigDecimal getBruto() {
        return bruto;
    }

    public BigDecimal getDescontos() {
        return descontos;
    }

    public BigDecimal getLiquido() {
        return bruto.subtract(descontos).setScale(2, RoundingMode.FLOOR);
    }

    /** Chamado uma única vez pelo empregado, logo depois de montar o contracheque. */
    void aplicarDescontos(BigDecimal descontos) {
        this.descontos = descontos;
    }

    public abstract void aceitar(VisitanteContracheque visitante);
}
