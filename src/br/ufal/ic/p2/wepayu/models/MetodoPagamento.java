package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.excecoes.EmpregadoNaoRecebeEmBancoException;

import java.io.Serializable;

/** Forma de pagamento de um empregado (em mãos, correios ou banco). */
public interface MetodoPagamento extends Serializable {

    /** emMaos / correios / banco - usado em getAtributoEmpregado e alteraEmpregado. */
    String getTag();

    /** Texto que aparece no relatório da folha. */
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
