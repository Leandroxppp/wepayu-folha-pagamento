package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.excecoes.AgenciaNulaException;
import br.ufal.ic.p2.wepayu.excecoes.BancoNuloException;
import br.ufal.ic.p2.wepayu.excecoes.ContaCorrenteNulaException;

public class Banco implements MetodoPagamento {
    private static final long serialVersionUID = 1L;

    private final String banco;
    private final String agencia;
    private final String contaCorrente;

    public Banco(String banco, String agencia, String contaCorrente)
            throws BancoNuloException, AgenciaNulaException, ContaCorrenteNulaException {
        if (banco == null || banco.trim().isEmpty()) throw new BancoNuloException();
        if (agencia == null || agencia.trim().isEmpty()) throw new AgenciaNulaException();
        if (contaCorrente == null || contaCorrente.trim().isEmpty()) throw new ContaCorrenteNulaException();
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    @Override public String getBanco() { return banco; }
    @Override public String getAgencia() { return agencia; }
    @Override public String getContaCorrente() { return contaCorrente; }
    @Override public String getTag() { return "banco"; }

    @Override
    public String descricao(String enderecoEmpregado) {
        return banco + ", Ag. " + agencia + " CC " + contaCorrente;
    }
}
