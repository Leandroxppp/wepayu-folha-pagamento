package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContrachequeAssalariado extends Contracheque {
    private static final long serialVersionUID = 1L;

    public ContrachequeAssalariado(LocalDate data, String nome, String metodo, BigDecimal bruto) {
        super(data, nome, metodo, bruto);
    }

    @Override
    public void aceitar(VisitanteContracheque visitante) {
        visitante.visitar(this);
    }
}
