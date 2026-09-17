package br.ufal.ic.p2.wepayu.models;

public class EmMaos implements MetodoPagamento {
    private static final long serialVersionUID = 1L;

    @Override
    public String getTag() {
        return "emMaos";
    }

    @Override
    public String descricao(String enderecoEmpregado) {
        return "Em maos";
    }
}
