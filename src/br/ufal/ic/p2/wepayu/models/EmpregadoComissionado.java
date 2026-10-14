package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.agenda.AgendasPadrao;
import br.ufal.ic.p2.wepayu.excecoes.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/** Assalariado que também recebe um percentual das vendas realizadas no período. */
public class EmpregadoComissionado extends EmpregadoAssalariado {
    private static final long serialVersionUID = 1L;

    private BigDecimal taxaComissao;

    public EmpregadoComissionado(String id, String nome, String endereco, BigDecimal salarioMensal,
                                 BigDecimal taxaComissao)
            throws NomeNuloException, EnderecoNuloException, SalarioNuloException, SalarioNegativoException,
            ComissaoNulaException, ComissaoNegativaException {
        super(id, nome, endereco, salarioMensal, AgendasPadrao.QUINZENAL_SEXTA);
        setComissao(taxaComissao);
    }

    public EmpregadoComissionado(Empregado origem, BigDecimal salarioMensal, BigDecimal taxaComissao)
            throws SalarioNuloException, SalarioNegativoException, ComissaoNulaException,
            ComissaoNegativaException {
        super(origem, salarioMensal, AgendasPadrao.QUINZENAL_SEXTA);
        setComissao(taxaComissao);
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.COMISSIONADO;
    }

    @Override
    public BigDecimal getComissao() {
        return taxaComissao;
    }

    @Override
    public void setComissao(BigDecimal comissao) throws ComissaoNulaException, ComissaoNegativaException {
        if (comissao == null) {
            throw new ComissaoNulaException();
        }
        if (comissao.signum() < 0) {
            throw new ComissaoNegativaException();
        }
        this.taxaComissao = comissao;
    }

    @Override
    public void lancarVenda(LocalDate data, BigDecimal valor) throws ValorNaoPositivoException {
        registrarVenda(new ResultadoVenda(data, valor));
    }

    @Override
    public BigDecimal getVendas(Periodo periodo) {
        return vendasNo(periodo).stream().map(ResultadoVenda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    protected Contracheque montarContracheque(Periodo periodo, LocalDate data) {
        BigDecimal fixo = calcularFixo();
        BigDecimal vendas = getVendas(periodo).setScale(2, RoundingMode.FLOOR);
        BigDecimal comissao = vendas.multiply(taxaComissao).setScale(2, RoundingMode.FLOOR);
        BigDecimal bruto = fixo.add(comissao).setScale(2, RoundingMode.FLOOR);
        return new ContrachequeComissionado(data, getNome(), descricaoMetodoPagamento(), fixo, vendas, comissao, bruto);
    }
}
