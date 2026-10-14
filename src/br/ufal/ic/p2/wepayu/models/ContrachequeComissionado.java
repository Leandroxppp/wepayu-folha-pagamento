package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContrachequeComissionado extends Contracheque {
    private static final long serialVersionUID = 1L;

    private final BigDecimal fixo;
    private final BigDecimal vendas;
    private final BigDecimal comissao;

    public ContrachequeComissionado(LocalDate data, String nome, String metodo, BigDecimal fixo,
                                    BigDecimal vendas, BigDecimal comissao, BigDecimal bruto) {
        super(data, nome, metodo, bruto);
        this.fixo = fixo;
        this.vendas = vendas;
        this.comissao = comissao;
    }

    public BigDecimal getFixo() {
        return fixo;
    }

    public BigDecimal getVendas() {
        return vendas;
    }

    public BigDecimal getComissao() {
        return comissao;
    }

    @Override
    public void aceitar(VisitanteContracheque visitante) {
        visitante.visitar(this);
    }
}
