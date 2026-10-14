package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EmpregadoHorista extends Empregado {
    private static final long serialVersionUID = 1L;

    private BigDecimal salarioPorHora;
    private List<CartaoPonto> cartoes = new ArrayList<>();

    public EmpregadoHorista(String id, String nome, String endereco, BigDecimal salarioPorHora) {
        super(id, nome, endereco);
        this.salarioPorHora = salarioPorHora;
    }

    @Override
    public String getTipo() {
        return "horista";
    }

    @Override
    public BigDecimal getSalarioBase() {
        return salarioPorHora;
    }

    @Override
    public void setSalarioBase(BigDecimal valor) {
        this.salarioPorHora = valor;
    }

    public List<CartaoPonto> getCartoes() {
        return cartoes;
    }
}
