package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoComissionado extends EmpregadoAssalariado {
    private static final long serialVersionUID = 1L;

    private BigDecimal taxaComissao;
    private List<ResultadoVenda> vendas = new ArrayList<>();

    public EmpregadoComissionado(String id, String nome, String endereco, BigDecimal salarioMensal,
                                  BigDecimal taxaComissao) {
        super(id, nome, endereco, salarioMensal);
        this.taxaComissao = taxaComissao;
    }

    @Override
    public String getTipo() {
        return "comissionado";
    }

    public BigDecimal getTaxaComissao() {
        return taxaComissao;
    }

    public void setTaxaComissao(BigDecimal taxaComissao) {
        this.taxaComissao = taxaComissao;
    }

    public List<ResultadoVenda> getVendas() {
        return vendas;
    }
}
