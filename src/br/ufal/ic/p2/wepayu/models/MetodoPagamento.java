package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.excecoes.EmpregadoNaoRecebeEmBancoException;

import java.io.Serializable;

/** Forma de pagamento de um empregado. */
public interface MetodoPagamento extends Serializable {
    String getTag();
    String descricao(String enderecoEmpregado);

    default String getBanco() throws EmpregadoNaoRecebeEmBancoException {
        throw new EmpregadoNaoRecebeEmBancoException();
    }

    default String getAgencia() throws EmpregadoNaoRecebeEmBancoException {
        throw new EmpregadoNaoRecebeEmBancoException();
    }

    default String getContaCorrente() throws EmpregadoNaoRecebeEmBancoException {
        throw new EmpregadoNaoRecebeEmBancoException();
    }
}
