package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.excecoes.WePayUException;

import java.math.BigDecimal;

/** Cria empregados novos e converte empregados existentes para outro tipo, sem perder o histórico. */
public final class FabricaEmpregados {

    private FabricaEmpregados() {
    }

    public static Empregado criar(TipoEmpregado tipo, String id, String nome, String endereco, BigDecimal salario,
                                  BigDecimal comissao) throws WePayUException {
        switch (tipo) {
            case HORISTA:
                return new EmpregadoHorista(id, nome, endereco, salario);
            case ASSALARIADO:
                return new EmpregadoAssalariado(id, nome, endereco, salario);
            default:
                return new EmpregadoComissionado(id, nome, endereco, salario, comissao);
        }
    }

    /**
     * Converte o empregado para o novo tipo mantendo id, dados pessoais, sindicato, método de pagamento,
     * cartões de ponto e vendas. O horista pode receber um novo salário por hora (senão mantém o valor
     * atual) e o comissionado precisa de uma comissão.
     */
    public static Empregado converter(Empregado origem, TipoEmpregado tipo, BigDecimal novoSalario,
                                      BigDecimal comissao) throws WePayUException {
        switch (tipo) {
            case HORISTA:
                return new EmpregadoHorista(origem, novoSalario != null ? novoSalario : origem.getSalarioBase());
            case ASSALARIADO:
                return new EmpregadoAssalariado(origem, origem.getSalarioBase());
            default:
                return new EmpregadoComissionado(origem, origem.getSalarioBase(), comissao);
        }
    }
}
