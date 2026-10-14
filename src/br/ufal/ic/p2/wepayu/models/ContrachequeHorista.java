package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContrachequeHorista extends Contracheque {
    private static final long serialVersionUID = 1L;

    private final BigDecimal horasNormais;
    private final BigDecimal horasExtras;

    public ContrachequeHorista(LocalDate data, String nome, String metodo, BigDecimal horasNormais,
                               BigDecimal horasExtras, BigDecimal bruto) {
        super(data, nome, metodo, bruto);
        this.horasNormais = horasNormais;
        this.horasExtras = horasExtras;
    }

    public BigDecimal getHorasNormais() {
        return horasNormais;
    }

    public BigDecimal getHorasExtras() {
        return horasExtras;
    }

    @Override
    public void aceitar(VisitanteContracheque visitante) {
        visitante.visitar(this);
    }
}
