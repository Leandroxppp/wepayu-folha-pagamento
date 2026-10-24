package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.agenda.AgendaPagamento;
import br.ufal.ic.p2.wepayu.agenda.AgendasPadrao;
import br.ufal.ic.p2.wepayu.excecoes.EnderecoNuloException;
import br.ufal.ic.p2.wepayu.excecoes.NomeNuloException;
import br.ufal.ic.p2.wepayu.excecoes.SalarioNegativoException;
import br.ufal.ic.p2.wepayu.excecoes.SalarioNuloException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmpregadoAssalariado extends Empregado {
    private static final long serialVersionUID = 1L;
    private BigDecimal salarioMensal;

    public EmpregadoAssalariado(String id, String nome, String endereco, BigDecimal salarioMensal)
            throws NomeNuloException, EnderecoNuloException, SalarioNuloException, SalarioNegativoException {
        this(id, nome, endereco, salarioMensal, AgendasPadrao.MENSAL_ULTIMO_DIA_UTIL);
    }

    protected EmpregadoAssalariado(String id, String nome, String endereco, BigDecimal salarioMensal,
                                   AgendaPagamento agenda)
            throws NomeNuloException, EnderecoNuloException, SalarioNuloException, SalarioNegativoException {
        super(id, nome, endereco, agenda);
        setSalarioBase(salarioMensal);
    }

    public EmpregadoAssalariado(Empregado origem, BigDecimal salarioMensal) throws SalarioNuloException, SalarioNegativoException {
        this(origem, salarioMensal, AgendasPadrao.MENSAL_ULTIMO_DIA_UTIL);
    }

    protected EmpregadoAssalariado(Empregado origem, BigDecimal salarioMensal, AgendaPagamento agenda)
            throws SalarioNuloException, SalarioNegativoException {
        super(origem, agenda);
        setSalarioBase(salarioMensal);
    }

    @Override public TipoEmpregado getTipo() { return TipoEmpregado.ASSALARIADO; }
    @Override public BigDecimal getSalarioBase() { return salarioMensal; }

    @Override
    public void setSalarioBase(BigDecimal valor) throws SalarioNuloException, SalarioNegativoException {
        if (valor == null) throw new SalarioNuloException();
        if (valor.compareTo(BigDecimal.ZERO) < 0) throw new SalarioNegativoException();
        salarioMensal = valor;
        limparCacheFolha();
    }

    @Override
    protected Contracheque calcularValores(LocalDate data) {
        BigDecimal bruto = getAgenda().calcularSalarioFixo(salarioMensal);
        return new ContrachequeAssalariado(data, getNome(), descricaoMetodoPagamento(), bruto);
    }
}
