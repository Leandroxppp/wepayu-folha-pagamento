package br.ufal.ic.p2.wepayu.models;

import java.math.BigDecimal;

public class EmpregadoAssalariado extends Empregado {
    private static final long serialVersionUID = 1L;

    protected BigDecimal salarioMensal;

    public EmpregadoAssalariado(String id, String nome, String endereco, BigDecimal salarioMensal) {
        super(id, nome, endereco);
        this.salarioMensal = salarioMensal;
    }

    @Override
    public String getTipo() {
        return "assalariado";
    }

    @Override
    public BigDecimal getSalarioBase() {
        return salarioMensal;
    }

    @Override
    public void setSalarioBase(BigDecimal valor) {
        this.salarioMensal = valor;
    }
}
