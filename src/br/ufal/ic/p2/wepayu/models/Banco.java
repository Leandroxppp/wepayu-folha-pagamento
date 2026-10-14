package br.ufal.ic.p2.wepayu.models;

public class Banco implements MetodoPagamento {
    private static final long serialVersionUID = 1L;

    private String banco;
    private String agencia;
    private String contaCorrente;

    public Banco(String banco, String agencia, String contaCorrente) {
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    public String getBanco() {
        return banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    @Override
    public String getTag() {
        return "banco";
    }

    @Override
    public String descricao(String enderecoEmpregado) {
        return banco + ", Ag. " + agencia + " CC " + contaCorrente;
    }
}
