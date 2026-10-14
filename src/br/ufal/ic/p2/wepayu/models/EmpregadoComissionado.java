package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.agenda.AgendasPadrao;
import br.ufal.ic.p2.wepayu.excecoes.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class EmpregadoComissionado extends EmpregadoAssalariado {
    private static final long serialVersionUID = 1L;
    private BigDecimal taxaComissao;

    public EmpregadoComissionado(String id, String nome, String endereco, BigDecimal salarioMensal,
                                 BigDecimal taxaComissao)
            throws NomeNuloException, EnderecoNuloException, SalarioNegativoException, ComissaoNegativaException,
            ComissaoNulaException {
        super(id, nome, endereco, salarioMensal, AgendasPadrao.QUINZENAL_SEXTA);
        if (taxaComissao == null) throw new ComissaoNulaException();
        setComissao(taxaComissao);
    }

    public EmpregadoComissionado(Empregado origem, BigDecimal salarioMensal, BigDecimal taxaComissao)
            throws SalarioNegativoException, ComissaoNegativaException, ComissaoNulaException {
        super(origem, salarioMensal, AgendasPadrao.QUINZENAL_SEXTA);
        if (taxaComissao == null) throw new ComissaoNulaException();
        setComissao(taxaComissao);
    }

    @Override public TipoEmpregado getTipo() { return TipoEmpregado.COMISSIONADO; }
    @Override public BigDecimal getComissao() { return taxaComissao; }

    @Override
    public void setComissao(BigDecimal valor) throws ComissaoNegativaException, ComissaoNulaException {
        if (valor == null) throw new ComissaoNulaException();
        if (valor.compareTo(BigDecimal.ZERO) < 0) throw new ComissaoNegativaException();
        taxaComissao = valor;
        limparCacheFolha();
    }

    @Override
    public void lancarVenda(LocalDate data, BigDecimal valor) throws ValorNaoPositivoException {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new ValorNaoPositivoException();
        registrarVenda(new ResultadoVenda(data, valor));
    }

    @Override
    public BigDecimal getVendas(Periodo periodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (ResultadoVenda venda : vendasNo(periodo)) total = total.add(venda.getValor());
        return total;
    }

    @Override
    protected Contracheque calcularValores(LocalDate data) {
        BigDecimal fixo = getAgenda().calcularSalarioFixo(getSalarioBase());
        BigDecimal vendas = getVendas(periodoDesdeUltimoPagamento(data)).setScale(2, RoundingMode.FLOOR);
        BigDecimal comissao = vendas.multiply(taxaComissao).setScale(2, RoundingMode.FLOOR);
        BigDecimal bruto = fixo.add(comissao).setScale(2, RoundingMode.FLOOR);
        return new ContrachequeComissionado(data, getNome(), descricaoMetodoPagamento(), fixo, vendas, comissao, bruto);
    }
}
