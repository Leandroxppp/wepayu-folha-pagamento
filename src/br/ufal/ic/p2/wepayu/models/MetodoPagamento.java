package br.ufal.ic.p2.wepayu.models;

import java.io.Serializable;

// forma de pagamento de um empregado (em maos, correios ou banco)
public interface MetodoPagamento extends Serializable {

    // emMaos / correios / banco - usado em getAtributoEmpregado e alteraEmpregado
    String getTag();

    // texto que aparece no relatorio da folha
    String descricao(String enderecoEmpregado);
}
