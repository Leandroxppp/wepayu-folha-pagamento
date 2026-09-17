package br.ufal.ic.p2.wepayu.models;

public class Correios implements MetodoPagamento {
    private static final long serialVersionUID = 1L;

    @Override
    public String getTag() {
        return "correios";
    }

    @Override
    public String descricao(String enderecoEmpregado) {
        return "Correios, " + enderecoEmpregado;
    }
}
